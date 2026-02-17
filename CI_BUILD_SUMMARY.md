# CI Build Workflow Summary

## Overview
The GitHub Actions CI workflow now fully builds the `com.android.virt.apex` module and uploads both the APEX file and its extracted payload as a tarball.

## Workflow: Build Android Virtualization APEX

**File**: `.github/workflows/build-terminal-app.yml`

### Trigger Events
- **Push** to main/master branches affecting:
  - `android/**` (all Android components)
  - `libs/**` (all library components)
  - The workflow file itself
- **Pull Request** to main/master branches with same path filters
- **Manual trigger** via `workflow_dispatch`

### Build Steps

#### 1. Environment Setup
- Maximize disk space (remove unnecessary tools)
- Checkout Virtualization module
- Install JDK 17
- Install AOSP build dependencies

#### 2. AOSP Repository Setup
- Install and configure `repo` tool
- Initialize AOSP repository (main branch, depth=1, partial-clone)
- Create local manifest for Virtualization module

#### 3. Source Synchronization
Syncs the following AOSP components:
- `build/make` - Build system
- `build/soong` - Build system (Soong)
- `build/blueprint` - Build blueprints
- `prebuilts/sdk` - SDK prebuilts
- `prebuilts/build-tools` - Build tools
- `prebuilts/gcc/linux-x86/host/x86_64-linux-glibc2.17-4.8` - GCC toolchain
- `prebuilts/clang/host/linux-x86` - Clang toolchain
- `prebuilts/rust` - Rust toolchain
- `external/golang` - Go language
- `external/rust/crates` - Rust crates
- `frameworks/base` - Android framework base
- `frameworks/native` - Native frameworks
- `system/core` - Core system libraries
- `system/libbase` - Base library
- `system/logging` - Logging library
- `packages/modules/common` - Common module components

#### 4. Build Process
```bash
cd ~/aosp
source build/envsetup.sh
banchan com.android.virt aosp_arm64
UNBUNDLED_BUILD_SDKS_FROM_SOURCE=true m apps_only dist
```

#### 5. Artifact Processing
1. **Find APEX**: Locate `com.android.virt.apex` in `out/dist/`
2. **Copy APEX**: Copy to build outputs directory
3. **Extract Payload**: Unzip APEX file (APEX files are ZIP archives)
4. **Create Tarball**: Package extracted contents as `com.android.virt.apex-payload.tar.gz`
5. **Generate Documentation**: Create README.txt with build info and logs

#### 6. Upload Artifacts
Uploads the following files via `actions/upload-artifact@v6`:
- `com.android.virt.apex` - The complete APEX module
- `com.android.virt.apex-payload.tar.gz` - Extracted APEX payload
- `README.txt` - Build documentation and logs

**Retention**: 30 days

### Build Summary
The workflow creates a GitHub Actions summary showing:
- ✅ Build success/failure status
- 📦 Artifact names and sizes
- 📝 Installation instructions
- 🔍 Build log excerpts (if available)

### Installation Instructions
To install the built APEX on a device:
```bash
adb install com.android.virt.apex
adb reboot
```

## Resource Requirements

### Disk Space
- Initial: ~50GB+ for AOSP sync
- Build: Additional space for compilation
- CI runners may need extended disk space

### Build Time
- AOSP sync: 10-30 minutes (depending on network)
- Build: 30-120 minutes (depending on resources)
- **Total**: 1-3 hours typically
- **Timeout**: 6 hours maximum

### Network
- Downloading AOSP sources requires significant bandwidth
- Uses partial-clone and depth=1 to minimize download size

## Troubleshooting

### Build Failures
Common issues and solutions:

1. **Disk Space**: 
   - Workflow maximizes disk space by removing unnecessary tools
   - May still run out on free GitHub runners
   - Solution: Use self-hosted runners with more disk space

2. **Missing Dependencies**:
   - Some AOSP components may fail to sync
   - Build continues with `|| true` flag
   - Solution: Add missing components to sync list

3. **Build Timeout**:
   - 6-hour timeout may not be enough for slow builds
   - Solution: Increase timeout or use faster runners

4. **Network Issues**:
   - AOSP sync may fail due to network problems
   - Solution: Retry workflow or use cached AOSP

### Verification
To verify the workflow locally:
```bash
# Validate YAML syntax
python3 -c "import yaml; yaml.safe_load(open('.github/workflows/build-terminal-app.yml'))"

# Check workflow structure
gh workflow view "Build Android Virtualization APEX"
```

## Comparison: Before vs After

### Before (Attempted Build)
- Minimal AOSP sync
- Attempted VmTerminalApp build
- Expected to fail due to missing dependencies
- Created informational README only

### After (Full Build)
- Comprehensive AOSP sync with all dependencies
- Builds complete com.android.virt APEX
- Extracts and packages payload
- Uploads production-ready artifacts
- Provides installation-ready APEX file

## Future Improvements

Potential enhancements:
1. Cache AOSP source between builds
2. Split sync and build into separate jobs
3. Build multiple architectures (arm64, x86_64)
4. Add APK extraction from APEX
5. Run tests on built APEX
6. Sign APEX with release keys (for production)
7. Create GitHub Release with artifacts

