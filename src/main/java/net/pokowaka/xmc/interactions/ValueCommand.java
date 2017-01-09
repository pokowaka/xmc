package net.pokowaka.xmc.interactions;


public enum ValueCommand {
    mode, // +1/-1 Mode up/down
    frequency, // +1/-1 Tuner Frequency up/down
    seek, // +1/-1 Tuner Seek up/down
    channel, // +1/-1 Tuner Preset Station up/down
    center,  //+n/-n Center Volume increment up/down
    subwoofer, //+n/-n Subwoofer Volume increment up/down
    surround, //+n/-n Surrounds Volume increment up/down
    back,  //+n/-n Backs Volume increment up/down
    input, //+n/-n Change Zone 1 Input up/down
    volume, //+n/-n Zone 1 Volume increment up/down
    set_volume,  //n Zone 1 Volume set level -96..11
    center_trim_set, // n Center Volume set level -12.0..+12.0
    subwoofer_trim_set, // n Subwoofer Volume set level -12.0..+12.0
    surround_trim_set, // n Surrounds Volume set level -12.0..+12.0
    back_trim_set, // n Backs Volume set level -12.0..+12.0
    zone2_volume,  //+n/-n Zone 2 Volume  increment up/down
    zone2_set_volume, //n Zone 2 Volume set level -96..11
    zone2_input, //+1/-1 Change Zone 2 Input up/down
}