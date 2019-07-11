/**
 * Classes in this package are expected to be run on a computer
 * instead of mobile phones.
 * <p>This package contains three sub-packages:
 * <ul>
 *  <li>adb: a wrapper of Android Debug Bridge to facilitate the communication
 *      between computer and usb-connected mobile phones.</li>
 *  <li>timing: the computer serves as a timing oracle for all usb-connected mobile phones.</li>
 *  <li>analysis: the computer collects executions from all usb-connected mobile phones and
 *      perform offline analysis on them.</li>
 * </ul>
 * </p>
 * @author ics-ant
 * @date 2016/01/29
 */
package io.github.hengxin.distributed_mobile_memo.pc;