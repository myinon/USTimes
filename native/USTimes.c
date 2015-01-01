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
			nNumberOfCharsToWrite = 2;
			lpNumberOfCharsWritten = 0;
			WriteConsoleW(err, (const void *) L"\r\n", nNumberOfCharsToWrite, &lpNumberOfCharsWritten, NULL);
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
	DWORD len;
	UINT uSize;
	int argc;
	
	ZeroMemory(classPath, sizeof(classPath));
	classPath[0] = L'\"';
	uSize = GetSystemDirectoryW(classPath + 1, ((UINT) COUNT) - 1);
	if (uSize > (COUNT - 1)) {
		displayError(L"System directory path name is too long.");
		return 1;
	}
	uSize++;
	
	if (lstrcpynW(classPath + uSize,
	L"\\javaw.exe\" -version:1.7* -jar ", 32) == NULL) {
		displayError(L"Could not copy javaw.exe to path.");
		return 2;
	}
	uSize += 32;
	
	classPath[uSize - 1] = L'\"';
	jarPath = ((LPWSTR) classPath) + uSize;
	len = GetModuleFileNameW(NULL, jarPath, ((DWORD) COUNT) - uSize);
	if (len == 0 || len >= (COUNT - uSize)) {
		displayError(L"Could not get module name.");
		return 3;
	}
	
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
				return 4;
			}
			
			if (lstrcatW(str, (LPWSTR) classPath) == NULL) {
				HeapFree(GetProcessHeap(), 0, (LPVOID) str);
				LocalFree((HLOCAL) argv);
				displayError(L"Can't copy command line arguments.");
				return 5;
			}
			
			for (i = 1; i < argc; i++) {
				if (lstrcatW(str, L" \"") == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 6;
				}
				
				if (lstrcatW(str, argv[i]) == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 7;
				}
				
				if (lstrcatW(str, L"\"") == NULL) {
					HeapFree(GetProcessHeap(), 0, (LPVOID) str);
					LocalFree((HLOCAL) argv);
					displayError(L"Can't copy command line arguments.");
					return 8;
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
		return 9;
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