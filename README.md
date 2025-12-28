# Lenovo Battery Conservation Mode Controller

A utility to control the battery conservation mode on Lenovo laptops. This tool provides both a terminal-based interface and a Java-based GUI to easily manage your laptop's battery conservation mode.

## What is Battery Conservation Mode?

Battery Conservation Mode is a Lenovo feature that limits battery charging to 55-60% of its capacity to extend the battery's lifespan. This is particularly useful when your laptop is primarily used while plugged into AC power.

## Prerequisites

- A Lenovo laptop with conservation mode support
- Linux operating system
- Root privileges (sudo access)

## Available Versions

### 1. Terminal-Based Version (toggle_conservation.sh)

This version provides a text-based user interface (TUI) that works in any terminal.

#### Requirements
- `dialog` (Install this manually on all devices except those running Ubuntu/Debian)

#### How to Use

1. Clone the repo :
   
   git clone https://github.com/humlink-dev/LCC.git

3. cd ConservationMode-Lenovo-

4. sudo bash -x toggle_conservation.sh

5. Navigate the menu:
   - Use ↑↓ arrow keys to select options
   - Press Enter to confirm selection
   - Press ESC or select Cancel to exit


## Features

- Real-time status display
- Easy toggle between enabled/disabled states
- Color-coded status indicators
- Error handling with informative messages
- Root privilege checking
- Automatic dependency installation (for terminal version)

## Troubleshooting

1. "Permission denied" error
   - Make sure to run the script with sudo
   - Check if the script is executable

2. "Conservation mode path not found" error
   - Verify that your laptop is a Lenovo model with conservation mode support
   - Check if the conservation mode file exists at:
     ```
     /sys/bus/platform/drivers/ideapad_acpi/VPC2004:00/conservation_mode
     ```

3. Java version not running
   - Ensure Java is installed:
     ```bash
     java -version
     ```
   - Install Java if needed:
     ```bash
     sudo apt-get install default-jdk
     ```

## Safety Notes

- The script requires root privileges as it modifies system files
- Always ensure your laptop is plugged in when changing conservation mode settings
- Changes take effect immediately but may require a few minutes to be reflected in the battery status

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is open source and available under the MIT License.

## Disclaimer

This tool directly modifies system files. While it includes safety checks, use it at your own risk. Always ensure your important work is saved before modifying system settings.
