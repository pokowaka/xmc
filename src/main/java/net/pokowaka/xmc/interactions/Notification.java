package net.pokowaka.xmc.interactions;

/**
 * Created by erwinj on 1/8/17.
 */
public enum Notification {
    power, //Zone 1 power “On”/”Off”
    source, //Zone 1 Input: “HDMI 1”, HDMI 2”, etc.
    dim, //Front Panel Dimness: “0”, “20”, “40”,”60”,”80”,”100”
    mode, //Zone 1 Mode: “Stereo”, “Direct”, “Auto”, etc.
    center, //Center Volume in dB
    subwoofer, //Subwoofer Volume in dB
    surround, //Surrounds Volume in dB
    back, //Backs Volume in dB
    volume, //Zone 1 Volume in dB
    loudness, //Zone 1 Loudness “On”/”Off”
    zone2_power, //Zone 2 power “On”/”Off”
    zone2_volume, //Zone 2 Volume in dB
    zone2_input, //Zone 2 Input: “HDMI 1”, HDMI 2”, etc.
    tuner_band, //Tuner Band: “AM” or “FM”
    tuner_channel, //User –assigned station name
    tuner_signal, //Tuner signal quality
    tuner_program, //“Country”, “Rock”, “Classical”, etc.
    tuner_RDS, //Tuner RDS string
    audio_input, //Audio Input: “HDMI 1”, HDMI 2”, etc.
    audio_bitstream, //Audio Bitstream: “PCM 2.0”, etc.
    audio_bits, //Audio Bits:”32kHz 24bits”, etc.
    video_input, //Video Input: “HDMI 1”, HDMI 2”, etc.
    video_format, //Video Format: “1920x1080i/60”, etc.
    video_space, //Video Space: “YCbCr 8bits”, etc.
    input_1, //User name assigned to Input Button 1
    input_2, //User name assigned to Input Button 2
    input_3, //User name assigned to Input Button 3
    input_4, //User name assigned to Input Button 4
    input_5, //User name assigned to Input Button 5
    input_6, //User name assigned to Input Button 6
    input_7, //User name assigned to Input Button 7
    input_8, //User name assigned to Input Button 8
    mode_ref_stereo, //"Reference Stereo"
    mode_stereo, //"Stereo"
    mode_music, //"Music"
    mode_movie, //"Movie"
    mode_direct, //"Direct"
    mode_dolby, //"Dolby"
    mode_dts, //"DTS"
    mode_all_stereo, //"All Stereo"
    mode_auto  //"Auto"
}