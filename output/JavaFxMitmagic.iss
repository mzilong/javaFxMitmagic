; 脚本由 Inno Setup 脚本向导 生成！
; iscc /OD:\work\IdeaProjects\JavaFX\javaFxMitmagic\target /FjavaFxMitmagic_1.0-SNAPSHOT D:\work\IdeaProjects\JavaFX\javaFxMitmagic\target\assets\javaFxMitmagic.iss


#define MyAppBaseName "javaFxMitmagic"     
#define MyAppPath  "D:\work\IdeaProjects\JavaFX\"+MyAppBaseName
#define MyAppReg  "javafx_mitmagic"

#define MyAppName "物理拓扑技术与应用"
#define MyAppVersion "1.0"
#define MyAppPublisher "mitmagic"
#define MyAppURL "http://www.mitmagic.com/"
#define MyAppExeName MyAppBaseName+".exe"
#define MyAppIconName MyAppBaseName+".ico"

[Setup]
; 注: AppId的值为单独标识该应用程序。
; 不要为其他安装程序使用相同的AppId值。
; (若要生成新的 GUID，可在菜单中点击 "工具|生成 GUID"。)
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
; [Icons] 的“quicklaunchicon”条目使用 {userappdata}，而其 [Tasks] 条目具有适合 IsAdminInstallMode 的检查。
UsedUserAreasWarning=no
;InfoBeforeFile=D:\JavaFxMitmagic\output\JavaFxMitmagic_display_information_before_installation.txt
;InfoAfterFile=D:\javaFxMitmagic\output\JavaFxMitmagic_display_information_after_installation.txt
; 以下行取消注释，以在非管理安装模式下运行（仅为当前用户安装）。
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
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl";InfoBeforeFile: "{#MyAppPath}\output\安装之前显示信息.txt";InfoAfterFile: "{#MyAppPath}\output\安装之后显示信息.txt"         
Name: "english"; MessagesFile: "compiler:Languages\English.isl";InfoBeforeFile: "{#MyAppPath}\output\display_information_before_installation.txt";InfoAfterFile: "{#MyAppPath}\output\display_information_after_installation.txt"

[CustomMessages]
english.MyAppName=Material topology technology and Application
chinesesimp.MyAppName=物理拓扑技术与应用
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

