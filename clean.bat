@echo off
@set TEMP_FILE1=temp1.txt

rem
rem classファイルの削除（javacの出力ファイル）
rem
for /r ./ %%L in (*.class) do del %%L

rem
rem javaファイルの削除（idljの出力ファイル） 
rem
@set KEY_WORD=IDL-to-Java
@findstr /s /m "%KEY_WORD%" *.java > %TEMP_FILE1%
for /f "tokens=* delims=" %%L in ( %TEMP_FILE1% ) do del %%L
@del %TEMP_FILE1%

rem
rem htmlファイルの削除（javadocの出力ファイル） 
rem
@set KEY_WORD='Generated by javadoc'
@findstr /s /m "%KEY_WORD%" *.html > %TEMP_FILE1%
for /f "tokens=* delims=" %%L in ( %TEMP_FILE1% ) do del %%L
@del %TEMP_FILE1%

rem
rem jarファイルの削除（jarの出力ファイル）
rem
for /r ./ %%L in (*aist*.jar) do del %%L

