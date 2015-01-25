#include <Windows.h>
#include <Shlwapi.h>

#define IDI_MAIN 101

#pragma comment(linker,"/manifestdependency:\"type='win32' name='Microsoft.Windows.Common-Controls' "\
"version='6.0.0.0' processorArchitecture='*' publicKeyToken='6595b64144ccf1df' language='*'\"")

void __stdcall displayError(LPCWSTR msg) {
	HANDLE err = GetStdHandle(STD_ERROR_HANDLE);
	MessageBoxW(NULL, msg, L"Error!", MB_OK | MB_ICONERROR);
	if (!((err == NULL) || (err == INVALID_HANDLE_VALUE))) {
		DWORD nNumberOfCharsToWrite = lstrlenW(msg);
		DWORD lpNumberOfCharsWritten = 0;
		BOOL bGood = WriteConsoleW(
			err, (const void *) msg, nNumberOfCharsToWrite, &lpNumberOfCharsWritten, NULL);
		if ((bGood == TRUE) && (nNumberOfCharsToWrite == lpNumberOfCharsWritten)) {
			nNumberOfCharsToWrite = 1;
			lpNumberOfCharsWritten = 0;
			WriteConsoleW(err, (const void *) L"\n", nNumberOfCharsToWrite, &lpNumberOfCharsWritten, NULL);
		}
	}
}

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPWSTR lpCmdLine, int nCmdShow) {
	STARTUPINFO startup;
	PROCESS_INFORMATION process;
	SECURITY_ATTRIBUTES attributes;
	WCHAR classPath[(2 * MAX_PATH) + 31];
	LPWSTR jarPath;
	LPWSTR *argv;
	SIZE_T COUNT = ((2 * MAX_PATH) + 31);
	HKEY hKey;
	HKEY hJre;
	LONG lError;
	DWORD lpType;
	DWORD lpcbData;
	LPBYTE version;
	DWORD len;
	UINT uSize;
	int argc;
	
	ZeroMemory(classPath, sizeof(classPath));
	classPath[0] = L'\"';
	
	lError = RegOpenKeyExW(HKEY_LOCAL_MACHINE, L"SOFTWARE\\JavaSoft\\Java Runtime Environment",
		0, KEY_READ, &hKey);
	if (lError != ERROR_SUCCESS) {
		displayError(L"Can't read Java registry information.");
		return 1;
	}
	
	lError = RegQueryValueExW(hKey, L"CurrentVersion", NULL, &lpType, NULL, &lpcbData);
	if ((lError != ERROR_SUCCESS) || (lpType != REG_SZ)) {
		RegCloseKey(hKey);
		displayError(L"Can't get Java version information.");
		return 2;
	}
	
	lpcbData += 1;
	version = (LPBYTE) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, (SIZE_T) lpcbData);
	if (version == NULL) {
		RegCloseKey(hKey);
		displayError(L"Out of memory.");
		return 3;
	}
	
	lError = RegQueryValueExW(hKey, L"CurrentVersion", NULL, &lpType, version, &lpcbData);
	if (lError != ERROR_SUCCESS) {
		RegCloseKey(hKey);
		displayError(L"Can't read Java version information.");
		return 4;
	}
	
	if (lstrcmpiW((LPWSTR) version, L"1.7") < 0) {
		HeapFree(GetProcessHeap(), 0, (LPVOID) version);
		RegCloseKey(hKey);
		displayError(L"Java version 1.7 or higher is required.");
		return 5;
	}
	
	lError = RegOpenKeyExW(hKey, (LPWSTR) version, 0, KEY_READ, &hJre);
	if (lError != ERROR_SUCCESS) {
		HeapFree(GetProcessHeap(), 0, (LPVOID) version);
		RegCloseKey(hKey);
		displayError(L"Can't find Java version information.");
		return 6;
	}
	
	HeapFree(GetProcessHeap(), 0, (LPVOID) version);
	RegCloseKey(hKey);
	lpcbData = (DWORD) (COUNT - 1);
	
	lError = RegQueryValueExW(hJre, L"JavaHome", NULL, &lpType, (LPBYTE) (classPath + 1), &lpcbData);
	if ((lError != ERROR_SUCCESS) || (lpType != REG_SZ)) {
		RegCloseKey(hJre);
		displayError(L"Can't read Java path information.");
		return 7;
	}
	
	RegCloseKey(hJre);
	
	/*uSize = GetSystemDirectoryW(classPath + 1, ((UINT) COUNT) - 1);
	if (uSize > (COUNT - 1)) {
		displayError(L"System directory path name is too long.");
		return 1;
	}*/
	uSize = lstrlenW(classPath);
	//uSize++; // Initial quote character
	
	if (lstrcpynW(classPath + uSize,
	L"\\bin\\javaw.exe\" -jar ", 22) == NULL) {
		displayError(L"Could not copy javaw.exe to path.");
		return 8;
	}
	uSize += 22;
	
	classPath[uSize - 1] = L'\"';
	jarPath = ((LPWSTR) classPath) + uSize;
	len = GetModuleFileNameW(NULL, jarPath, ((DWORD) COUNT) - uSize);
	if (len == 0 || len >= (COUNT - uSize)) {
		displayError(L"Could not get module name.");
		return 9;
	}
	classPath[len + uSize] = L'\"';
	
	jarPath = classPath;
	argv = CommandLineToArgvW(GetCommandLineW(), &argc);
	if (argv != NULL) {
		int i;
		LPWSTR str;
		len = 0;
		for (i = 1; i < argc; i++) {
			len += lstrlenW(argv[i]);
			len += 3; // 2 " and space
		}
		
		if (len != 0) {
			len += lstrlenW(classPath);
			
			str = (LPWSTR) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY,
				(SIZE_T) ((len + 1) * sizeof(WCHAR)));
			if (str == NULL) {
				displayError(L"Can't create command line arguments.");
				return 10;
			}
			
			if (lstrcatW(str, (LPWSTR) classPath) == NULL) {
				HeapFree(GetProcessHeap(), 0, (LPVOID) str);
				LocalFree((HLOCAL) argv);
				displayError(L"Can't copy command line arguments.");
				return 11;
			}
			
			for (i = 1; i < argc; i++) {
				if (lstrcatW(str, L" \"") == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 12;
				}
				
				if (lstrcatW(str, argv[i]) == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 13;
				}
				
				if (lstrcatW(str, L"\"") == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 14;
				}
			}
			jarPath = str;
		}
		LocalFree((HLOCAL) argv);
	}
	
	ZeroMemory(&startup, sizeof(STARTUPINFO));
	ZeroMemory(&process, sizeof(PROCESS_INFORMATION));
	ZeroMemory(&attributes, sizeof(SECURITY_ATTRIBUTES));
	startup.cb = sizeof(STARTUPINFO);
	attributes.bInheritHandle = TRUE;
	attributes.lpSecurityDescriptor = NULL;
	attributes.nLength = sizeof(SECURITY_ATTRIBUTES);
	
	if (!CreateProcessW(NULL, jarPath, &attributes, &attributes,
	TRUE, CREATE_NEW_PROCESS_GROUP /*| CREATE_NO_WINDOW*/, NULL, NULL, &startup, &process)) {
		if (jarPath != classPath)
			HeapFree(GetProcessHeap(), 0, (LPVOID) jarPath);
		displayError(L"Could not create java process.");
		return 15;
	} else {
		CloseHandle(process.hThread);
		CloseHandle(process.hProcess);
	}
	if (jarPath != classPath)
		HeapFree(GetProcessHeap(), 0, (LPVOID) jarPath);
	
	UNREFERENCED_PARAMETER(hInstance);
	UNREFERENCED_PARAMETER(hPrevInstance);
	UNREFERENCED_PARAMETER(lpCmdLine);
	UNREFERENCED_PARAMETER(nCmdShow);
	return 0;
}