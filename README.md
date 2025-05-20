# Huffman Coding & Steganography Tool

## Project Details
This Java application implements Huffman coding for text compression and steganography for hiding and extracting messages in images (PNG/JPEG) and WAV audio files. It includes a graphical user interface (GUI) built with Swing and uses the Sardinas-Patterson algorithm to verify if a set of binary codes is uniquely decodable. The project is organized into two packages:

### Package: `com.huffman.core`
- **HuffmanCoding.java**: Implements Huffman coding.
  - Generates variable-length prefix codes based on character frequencies.
  - Encodes text into binary strings and decodes them using a Huffman dictionary.
- **ImageProcessor.java**: Handles image-based steganography.
  - Generates random pixel positions and extracts bits from the least significant bit of the red channel.
- **WavProcessor.java**: Handles audio-based steganography.
  - Generates random bit positions and extracts bits from specified positions in WAV audio data.
- **SardinasPatterson.java**: Implements the Sardinas-Patterson algorithm to check if a set of codes is uniquely decodable.

### Package: `com.huffman.ui`
- **HuffmanUI.java**: Provides the GUI with text areas for input/output and buttons for encoding, decoding, media loading, and code checking.
  - Supports manual and automatic decoding from media.
  - Allows input of custom Huffman dictionaries.

## How to Use
The application is launched by running `run.bat`, which opens a GUI with input/output areas and buttons. Below is a guide to using each button and feature:

### Interface Components
- **Input Text Area**: Enter text to encode, binary codes to check, or a dictionary for decoding.
- **Output Text Area**: Displays results (encoded text, decoded messages, compression stats, or code check results).
- **Image Panel**: Shows a preview of the loaded image or indicates a loaded WAV file.
- **Status Label**: Displays the current status or error messages.
- **Buttons**:
  - **Encode**: Compress text using Huffman coding.
  - **Decode from Media**: Extract hidden messages from images or WAV files.
  - **Load Image**: Load a PNG or JPEG file for steganography.
  - **Load WAV**: Load a WAV file for steganography.
  - **Compression Stats**: Show compression details for the input text.
  - **Decode from Dictionary**: Decode a binary sequence using the current dictionary.
  - **Check Code**: Verify if a set of binary codes is uniquely decodable.
  - **Add Dictionary**: Input a custom Huffman dictionary for decoding.

### Button Usage
1. **Encode**:
   - **Action**: Enter text in the input text area and click "Encode".
   - **Result**: Generates Huffman codes, encodes the text into a binary string, and displays the encoded text and dictionary in the output text area.
   - **Note**: Clears any custom dictionary and generates a new one based on the input text.

2. **Load Image**:
    - **Action**: Click to open a file chooser and select a PNG or JPEG image.
    - **Result**: Loads the image, displays a scaled preview (if wider than 300 pixels), and updates the status label with the file name.
    - **Note**: Required for image-based decoding.

3. **Load WAV**:
    - **Action**: Click to open a file chooser and select a WAV file.
    - **Result**: Loads the audio data, displays "WAV file loaded" in the image panel, and updates the status label with the file name.
    - **Note**: Required for WAV-based decoding.

4. **Decode from Media**:
    - **Action**: Click to open a dialog with four options (if media is loaded):
    - **Auto Image Decode**: Extracts bits from 200 random pixel positions in the loaded image.
    - **Manual Image Decode**: Prompts for pixel coordinates (x,y per line).
    - **Auto WAV Decode**: Extracts bits from 100 random bit positions in the loaded WAV file.
    - **Manual WAV Decode**: Prom locking for bit positions (one per line).
    - **Result**: Extracts bits from the specified positions, decodes them using the current dictionary (generated or custom), and displays the bits, decoded text, and dictionary in the output text area.
    - **Note**: Requires a loaded image/WAV and a valid dictionary. Custom dictionary is used if set.

5. **Compression Stats**:
    - **Action**: Enter text in the input text area and click "Compression Stats".
    - **Result**: Displays original text, encoded binary, original size (bits), encoded size (bits), compression ratio, and Huffman dictionary.

6. **Decode from Dictionary**:
    - **Action**: Click to open a dialog, enter a binary sequence (e.g., `1101110010`), and click "Decode".
    - **Result**: Decodes the sequence using the current dictionary and displays the binary input, decoded text, and dictionary.

7. **Check Code**:
    - **Action**: Enter a set of binary codes (comma-separated, one per line, or char:code format) in the input text area and click "Check Code".
    - **Result**: Uses the Sardinas-Patterson algorithm to check if the codes are uniquely decodable and displays the result.
    - **Note**: Codes must contain only 0s and 1s. Dictionary format requires single characters.

8. **Add Dictionary**:
    - **Action**: Click to open a dialog, enter a dictionary (char:code per line, e.g., `a:0\nb:10\nc:11`), and click "Add".
    - **Result**: Validates the dictionary (must be a prefix code), sets it as the custom dictionary for decoding, and displays it.


        