# Building VmTerminalApp with AOSP

This repository now uses the AOSP build system for the Terminal App.

## Quick Start (GitHub Actions CI)

The CI workflow automatically attempts to build the APK using AOSP tools when code is pushed.
See `.github/workflows/build-terminal-app.yml` for the automated build process.

## Local Build Instructions

### Prerequisites

1. Linux machine (Ubuntu 18.04+ recommended) with:
   - At least 250GB free disk space
   - 16GB RAM minimum (32GB recommended)
   - Fast internet connection

2. Install required packages:
```bash
sudo apt-get install git-core gnupg flex bison build-essential zip curl \
  zlib1g-dev gcc-multilib g++-multilib libc6-dev-i386 libncurses5 \
  lib32ncurses5-dev x11proto-core-dev libx11-dev lib32z1-dev \
  libgl1-mesa-dev libxml2-utils xsltproc unzip fontconfig python3 \
  python-is-python3 rsync
```

3. Install repo tool:
```bash
mkdir -p ~/.bin
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/.bin/repo
chmod a+rx ~/.bin/repo
export PATH="$HOME/.bin:$PATH"
```

### Build Steps

1. **Initialize AOSP repository:**
```bash
mkdir ~/aosp && cd ~/aosp
repo init -u https://android.googlesource.com/platform/manifest -b main
```

2. **Sync AOSP sources** (this takes time and downloads ~100GB+):
```bash
repo sync -c -j$(nproc)
```

3. **Set up build environment:**
```bash
source build/envsetup.sh
```

4. **Build the Virtualization APEX** (includes TerminalApp):
```bash
banchan com.android.virt aosp_arm64
UNBUNDLED_BUILD_SDKS_FROM_SOURCE=true m apps_only dist
```

5. **Locate the built APK:**
```bash
find out -name "VmTerminalApp*.apk"
```

The APK will typically be located at:
`out/target/product/*/system/priv-app/VmTerminalApp/VmTerminalApp.apk`

### Alternative: Build Only VmTerminalApp

```bash
source build/envsetup.sh
banchan VmTerminalApp aosp_arm64
m VmTerminalApp
```

## CI Build Limitations

The GitHub Actions CI workflow attempts an AOSP build but faces these limitations:

- **Disk Space**: AOSP requires 100GB+, CI runners have limited space
- **Build Time**: Full builds can take hours, CI has timeout limits  
- **Dependencies**: Full dependency tree requires complete AOSP source

The CI workflow uses a minimal sync strategy that may not complete successfully but provides:
- Build attempt logs for debugging
- Comprehensive README for local builds
- APK artifact if build succeeds

## Gradle Build Files

Note: Gradle build files (`build.gradle.kts`, etc.) are present in the repository from a previous attempt to build without AOSP. These are **not** used by the current CI workflow and can be ignored. The app requires AOSP-specific framework classes that are not available in public Maven repositories.

## More Information

- [Android Virtualization Framework Documentation](https://source.android.com/docs/core/virtualization)
- [AOSP Build Instructions](https://source.android.com/docs/setup/build/building)
- [AVF Getting Started Guide](./docs/getting_started.md)

## Troubleshooting

**Build fails with missing dependencies:**
- Ensure full AOSP sync completed successfully
- Check that you're using the correct AOSP branch (main)

**Out of disk space:**
- AOSP requires at least 250GB free space
- Use `ccache` to speed up subsequent builds

**Build takes too long:**
- Use `-j$(nproc)` to parallelize builds
- Consider using a faster machine or build server
- Enable `ccache` for faster incremental builds

## Support

For issues related to:
- **AVF/Virtualization**: Check [AVF documentation](https://source.android.com/docs/core/virtualization)
- **AOSP Build**: See [AOSP build troubleshooting](https://source.android.com/docs/setup/build/building)
- **This Repository**: Open an issue on GitHub
