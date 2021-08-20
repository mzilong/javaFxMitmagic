; �ű��� Inno Setup �ű��� ���ɣ�
; iscc /OD:\work\IdeaProjects\JavaFX\javaFxMitmagic\target /FjavaFxMitmagic_1.0-SNAPSHOT D:\work\IdeaProjects\JavaFX\javaFxMitmagic\target\assets\javaFxMitmagic.iss


#define MyAppBaseName "javaFxMitmagic"     
#define MyAppPath  "D:\work\IdeaProjects\JavaFX\"+MyAppBaseName
#define MyAppReg  "javafx_mitmagic"

#define MyAppName "�������˼�����Ӧ��"
#define MyAppVersion "1.0"
#define MyAppPublisher "mitmagic"
#define MyAppURL "http://www.mitmagic.com/"
#define MyAppExeName MyAppBaseName+".exe"
#define MyAppIconName MyAppBaseName+".ico"

[Setup]
; ע: AppId��ֵΪ������ʶ��Ӧ�ó���
; ��ҪΪ������װ����ʹ����ͬ��AppIdֵ��
; (��Ҫ�����µ� GUID�����ڲ˵��е�� "����|���� GUID"��)
AppId={{5FC7C2BF-FF80-4C70-858C-FCF00B614D74}
AppName={cm:MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppBaseName}_{#MyAppVersion} 
DisableProgramGroupPage=yes
; [Icons] �ġ�quicklaunchicon����Ŀʹ�� {userappdata}������ [Tasks] ��Ŀ�����ʺ� IsAdminInstallMode �ļ�顣
UsedUserAreasWarning=no
;InfoBeforeFile=D:\JavaFxMitmagic\output\JavaFxMitmagic_display_information_before_installation.txt
;InfoAfterFile=D:\javaFxMitmagic\output\JavaFxMitmagic_display_information_after_installation.txt
; ������ȡ��ע�ͣ����ڷǹ���װģʽ�����У���Ϊ��ǰ�û���װ����
;PrivilegesRequired=lowest  
PrivilegesRequired=admin
OutputDir={#MyAppPath}\target\{#MyAppBaseName}\windows
OutputBaseFilename={#MyAppBaseName}_{#MyAppVersion}_setup
SetupIconFile={#MyAppPath}\target\{#MyAppBaseName}\windows\{#MyAppBaseName}\{#MyAppIconName}
;Password=mitmagic
;Encryption=yes
Compression=lzma
SolidCompression=yes
WizardStyle=modern
VersionInfoProductName={cm:MyAppName}
VersionInfoDescription={cm:MyAppName}_Setup 
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl";InfoBeforeFile: "{#MyAppPath}\output\��װ֮ǰ��ʾ��Ϣ.txt";InfoAfterFile: "{#MyAppPath}\output\��װ֮����ʾ��Ϣ.txt"         
Name: "english"; MessagesFile: "compiler:Languages\English.isl";InfoBeforeFile: "{#MyAppPath}\output\display_information_before_installation.txt";InfoAfterFile: "{#MyAppPath}\output\display_information_after_installation.txt"

[CustomMessages]
english.MyAppName=Material topology technology and Application
chinesesimp.MyAppName=�������˼�����Ӧ��
english.Language=en
chinesesimp.Language=zh

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked; OnlyBelowVersion: 6.1; Check: not IsAdminInstallMode

[Files]
Source: "{#MyAppPath}\target\javaFxMitmagic\windows\javaFxMitmagic\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{autoprograms}\{cm:MyAppName}"; Filename: "{app}\{#MyAppExeName}";IconFilename: "{app}\{#MyAppIconName}"
Name: "{autodesktop}\{cm:MyAppName}"; Filename: "{app}\{#MyAppExeName}";IconFilename: "{app}\{#MyAppIconName}"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{cm:MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\{#MyAppIconName}";Tasks: quicklaunchicon


[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{cm:MyAppName}}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKLM; Subkey: "Software\JavaSoft\Prefs\{#MyAppReg}"; Flags: uninsdeletekey;Permissions:users-full
Root: HKLM; Subkey: "Software\JavaSoft\Prefs\{#MyAppReg}"; ValueType: string; ValueName: "language"; ValueData: "{cm:language}";Permissions:users-full

