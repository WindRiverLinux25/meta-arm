SUMMARY = "OpenCSD - An open source CoreSight(tm) Trace Decode library"
HOMEPAGE = "https://github.com/Linaro/OpenCSD"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad8cb685eb324d2fa2530b985a43f3e5"

SRC_URI = "git://github.com/Linaro/OpenCSD;protocol=https;branch=master"
SRC_URI:append:marvell-cn96xx = "${MARVELL_PATCHES}"
SRC_URI:append:marvell-cn10xxx = "${MARVELL_PATCHES}"

SRCREV = "1900c6fd110275305b956aa47bd9f0c76b13a134"

S = "${WORKDIR}/git"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

EXTRA_OEMAKE = "ARCH='${TARGET_ARCH}' \
                CROSS_COMPILE='${TARGET_SYS}-' \
                CC='${CC}' \
                CXX='${CXX}' \
                LIB='${AR}' \
                LINKER='${CXX}' \
                LINUX64=1 \
                DEBUG=1 \
                PREFIX=${D}/usr \
                INSTALL_BIN_DIR=${D}${bindir} \
                INSTALL_INCLUDE_DIR=${D}${includedir} \
                INSTALL_LIB_DIR=${D}${libdir} \
                INSTALL_MAN_DIR=${D}${mandir}/man1 \
                "

do_compile() {
    oe_runmake -C ${S}/decoder/build/linux
}

do_install() {
    oe_runmake -C ${S}/decoder/build/linux install install_man
}

MARVELL_PATCHES = " \
    file://0001-otx2_snapshot-Add-otx2-snapshot-support.patch \
    file://0002-packages-opencsd-Fix-trace-decode-for-spaces-in-disa.patch \
    file://0003-packages-opencsd-Update-error-message-in-script.patch \
    file://0004-packages-opencsd-add-a-script-for-sysfs.patch \
    file://0005-c_api_pkt_print_test-add-support-for-cn10k-chips.patch \
    file://0006-opencsd-add-perf-instruction-trace-script.patch \
    file://0007-coresight-trace-Add-support-for-kernel-symbols.patch \
    file://0008-coreisght-trace-Add-support-for-timestamp.patch \
    file://0009-coresight-trace-Handle-no-timestamps-in-trace.patch \
    file://0010-cti-Add-CTI-config-script-for-stopping-trace-on-pani.patch \
    "

do_install:append:marvell-cn96xx() {
    marvell_install_function
}

do_install:append:marvell-cn10xxx() {
    marvell_install_function
}

marvell_install_function() {
    install -d ${D}/usr/share/opencsd
    install -m 755 ${S}/decoder/tests/bin/builddir/c_api_pkt_print_test ${D}/usr/bin/
    install -m 755 ${S}/coresight-trace.py ${D}/usr/share/opencsd/
    install -m 755 ${S}/coresight-sysfs.sh ${D}/usr/bin/
    install -m 755 ${S}/coresight-cti-config.sh ${D}/usr/bin/
    install -m 755 ${S}/arm-cs-trace-disasm.py ${D}/usr/bin/
    install -m 755 ${S}/README-coresight-sysfs.txt ${D}/usr/share/opencsd/
}

BBCLASSEXTEND = "native"

# Required by /usr/bin/coresight-sysfs.sh
RDEPENDS:${PN} += "bash"
