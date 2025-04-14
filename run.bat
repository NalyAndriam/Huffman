@echo off
set PROJECT_DIR=%CD%

:: Créer un dossier pour les fichiers compilés s'il n'existe pas
if not exist "%PROJECT_DIR%\bin" mkdir "%PROJECT_DIR%\bin"

:: Compiler les fichiers Java
javac -d "%PROJECT_DIR%\bin" "%PROJECT_DIR%\src\com\huffman\core\HuffmanCoding.java" ^
    "%PROJECT_DIR%\src\com\huffman\core\ImageProcessor.java" ^
    "%PROJECT_DIR%\src\com\huffman\core\WavProcessor.java" ^
    "%PROJECT_DIR%\src\com\huffman\ui\HuffmanUI.java" ^
    "%PROJECT_DIR%\src\Main.java"

:: Vérifier si la compilation a réussi
if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie. Lancement de l'application...
    :: Lancer l'application
    java -cp "%PROJECT_DIR%\bin" Main
) else (
    echo Erreur lors de la compilation.
)
pause