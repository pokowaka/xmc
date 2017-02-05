package net.pokowaka.xmc.interactions;

/**
 * A Simple Command is a command that takes no parameters.
 * Usually the command speaks for it self.
 */
public enum SimpleCommand {
    none, //0 No command.  Ignored.
    standby, //0 Enter standby mode
    source_tuner, //0 Set source to Tuner
    source_1, //0 Set source to Input 1
    source_2, //0 Set source to Input 2
    source_3, //0 Set source to Input 3
    source_4, //0 Set source to Input 4
    source_5, //0 Set source to Input 5
    source_6, //0 Set source to Input 6
    source_7, //0 Set source to Input 7
    source_8, //0 Set source to Input 8
    left, //0 Menu Left
    right, //0 Menu Right
    enter, //0 Menu Enter
    dim, //0 Cycle through FP dimness settings
    info, //0 Show Info screen
    mute, //0 Zone 1 Mute Toggle
    mute_on, //0 Zone 1 Mute on
    mute_off, //0 Zone 1 Mute off
    reference_stereo, //0 Set Mode to Reference Stereo
    music, //0 Select Music preset
    movie, //0 Select Movie preset
    input_up, //0 Zone 1 Input selection increment up
    input_down, //0 Zone 1 Input selection increment down
    power_on, //0 Zone 1 Power On
    power_off, //0 Zone 1 Power Off
    loudness_on, //0 Loudness On
    loudness_off, //0 Loudness Off
    loudness, //0 Toggle Zone 1 Loudness on/off
    speaker_preset, //0 Cycle through Speaker Presets
    mode_up, //0 Mode increment up
    mode_down, //0 Mode increment down
    bass_up, //0 Bass level increment up
    bass_down, //0 Bass level increment down
    treble_up, //0 Treble level increment up
    treble_down, //0 Treble level increment down
    zone2_power, //0 Toggle Zone 2 Power On/Off
    zone2_power_off, //0 Zone 2 Power Off
    zone2_power_on, //0 Zone 2 Power On
    zone1_band, //0 Toggle Tuner  Band AM/FM (also changes tuner in Zone 2)
    band_am, //0 Set Tuner  Band AM (changes tuner in Zone 1 and Zone 2)
    band_fm, //0 Set Tuner  Band FM (changes tuner in Zone 1 and Zone 2)
    zone2_mute, //0 Toggle Zone 2 Mute
    zone2_mute_off, //0 Zone 2 Mute Off
    zone2_mute_on, //0 Zone 2 Mute On
    zone2_band, //0 Not implemented
    direct, // 0 Select mode Stereo
    dolby, //0 Select mode Dolby
    dts, //0 Select mode DTS
    all_stereo, //0 Select mode All Stereo
    auto, //0 Select mode Auto
    preset1, //0 Select speaker preset 1
    preset2, //0 Select speaker preset 2
    dirac, //0 Select speaker DIRAC
    hdmi1, //0 Select input HDMI 1
    hdmi2, //0 Select input HDMI 2
    hdmi3, //0 Select input HDMI 3
    hdmi4, //0 Select input HDMI 4
    hdmi5, //0 Select input HDMI 5
    hdmi6, //0 Select input HDMI 6
    hdmi7, //0 Select input HDMI 7
    hdmi8, //0 Select input HDMI 8
    coax1, //0 Select input Coax 1
    coax2, //0 Select input Coax 2
    coax3, //0 Select input Coax 3
    coax4, //0 Select input Coax 4
    optical1, //0 Select input Optical 1
    optical2, //0 Select input Optical 2
    optical3, //0 Select input Optical 3
    optical4, //0 Select input Optical 4
    ARC, //0 Select input ARC
    usb_stream, //0 Select input USB stream
    tuner, //0 Select input Tuner 1
    analog1, //0 Select input Analog 1
    analog2, //0 Select input Analog 2
    analog3, //0 Select input Analog 3 4
    analog4, //0 Select input Analog 5
    analog5, //0 Select input Analog 7.1
    analog7, //.1 0 Select input Analog
    front_in, //0 Select input Front
    zone2_analog1, //0 Select Zone 2 input Analog 1
    zone2_analog2, //0 Select Zone 2 input Analog 2
    zone2_analog3, //0 Select Zone 2 input Analog 3
    zone2_analog4, //0 Select Zone 2 input Analog 4
    zone2_analog5, //0 Select Zone 2 input Analog 5
    zone2_analog71, //0 Select Zone 2 input Analog 7.1
    zone2_analog8, //0 Select Zone 2 input Analog 8
    zone2_front_in, //0 Select Zone 2 input Front
    zone2_ARC, //0 Select Zone 2 input ARC
    zone2_ethernet, //0 Select Zone 2 input Ethernet
    zone2_follow_main, //0 Select Zone 2 input Follow Main
    zone2_coax1, //0 Select Zone 2 input Coax 1
    zone2_coax2, //0 Select Zone 2 input Coax 2
    zone2_coax3, //0 Select Zone 2 input Coax 3
    zone2_coax4, //0 Select Zone 2 input Coax 4
    zone2_optical1, //0 Select Zone 2 input Optical 1
    zone2_optical2, //0 Select Zone 2 input Optical 2
    zone2_optical3, //0 Select Zone 2 input Optical 3
    zone2_optical4, //0 Select Zone 2 input Optical 4
    channel_1, //0 Select Tuner Station 1
    channel_2, //0 Select Tuner Station 2
    channel_3, //0 Select Tuner Station 3
    channel_4, //0 Select Tuner Station 4
    channel_5, //0 Select Tuner Station 5
    channel_6, //0 Select Tuner Station 6
    channel_7, //0 Select Tuner Station 7
    channel_8, //0 Select Tuner Station 8
    channel_9, //0 Select Tuner Station 9
    channel_10, //0 Select Tuner Station 10
    channel_11, //0 Select Tuner Station 11
    channel_12, //0 Select Tuner Station 12
    channel_13, //0 Select Tuner Station 13
    channel_14, //0 Select Tuner Station 14
    channel_15, //0 Select Tuner Station 15
    channel_16, //0 Select Tuner Station 16
    channel_17, //0 Select Tuner Station 17
    channel_18, //0 Select Tuner Station 18
    channel_19, //0 Select Tuner Station 19
    channel_20 //0 Select Tuner Station 20
}
