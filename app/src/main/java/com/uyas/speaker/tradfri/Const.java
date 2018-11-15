package com.uyas.speaker.tradfri;

public class Const {
    public final static String ROOT_DEVICES = "15001";
    public final static String ROOT_GATEWAY = "15011";
    public final static String ROOT_GROUPS = "15004";
    public final static String ROOT_MOODS = "15005";
    public final static String ROOT_NOTIFICATION = "15006";  // speculative name
    public final static String ROOT_SMART_TASKS = "15010";
    public final static String ROOT_START_ACTION = "15013";  // found under ATTR_START_ACTION
    public final static String ROOT_SWITCH = "15009";

    public final static String ATTR_ALEXA_PAIR_STATUS = "9093";
    public final static String ATTR_AUTH = "9063";
    public final static String ATTR_APPLICATION_TYPE = "5750";

    public final static String ATTR_CERTIFICATE_PEM = "9096";
    public final static String ATTR_CERTIFICATE_PROV = "9092";
    public final static String ATTR_CLIENT_IDENTITY_PROPOSED = "9090";
    public final static String ATTR_CREATED_AT = "9002";
    public final static String ATTR_COGNITO_ID = "9101";
    public final static String ATTR_COMMISSIONING_MODE = "9061";
    public final static String ATTR_CURRENT_TIME_UNIX = "9059";
    public final static String ATTR_CURRENT_TIME_ISO8601 = "9060";

    public final static String ATTR_DEVICE_INFO = "3";
    public final static String ATTR_DEVICE_VENDOR = "0";
    public final static String ATTR_DEVICE_PRODUCT = "1";

    public final static String ATTR_GATEWAY_TIME_SOURCE = "9071";
    public final static String ATTR_GATEWAY_UPDATE_PROGRESS = "9055";

    public final static String ATTR_HOMEKIT_ID = "9083";
    public final static String ATTR_HS_LINK = "15002";

    public final static String ATTR_ID = "9003";
    public final static String ATTR_IDENTITY = "9090";
    public final static String ATTR_IOT_ENDPOINT = "9103";

    public final static String ATTR_KEY_PAIR = "9097";

    public final static String ATTR_LAST_SEEN = "9020";
    public final static String ATTR_LIGHT_CONTROL = "3311";  // array

    public final static String ATTR_MASTER_TOKEN_TAG = "9036";

    public final static String ATTR_NAME = "9001";
    public final static String ATTR_NTP = "9023";
    public final static String ATTR_FIRMWARE_VERSION = "9029";
    public final static String ATTR_FIRST_SETUP = "9069";  // ??? unix epoch value when gateway first setup

    public final static String ATTR_GATEWAY_INFO = "15012";
    public final static String ATTR_GATEWAY_ID = "9081";  // ??? id of the gateway
    public final static String ATTR_GATEWAY_REBOOT = "9030";  // gw reboot
    public final static String ATTR_GATEWAY_FACTORY_DEFAULTS = "9031";  // gw to factory defaults
    public final static String ATTR_GATEWAY_FACTORY_DEFAULTS_MIN_MAX_MSR = "5605";
    public final static String ATTR_GOOGLE_HOME_PAIR_STATUS = "9105";

    public final static String ATTR_DEVICE_STATE = "5850";  // 0 / 1
    public final static String ATTR_LIGHT_DIMMER = "5851";  // Dimmer, not following spec: 0..255
    public final static String ATTR_LIGHT_COLOR_HEX = "5706";  // string representing a value in hex
    public final static String ATTR_LIGHT_COLOR_X = "5709";
    public final static String ATTR_LIGHT_COLOR_Y = "5710";
    public final static String ATTR_LIGHT_COLOR_HUE = "5707";
    public final static String ATTR_LIGHT_COLOR_SATURATION = "5708";
    public final static String ATTR_LIGHT_MIREDS = "5711";

    public final static String ATTR_NOTIFICATION_EVENT = "9015";
    public final static String ATTR_NOTIFICATION_NVPAIR = "9017";
    public final static String ATTR_NOTIFICATION_STATE = "9014";

    public final static String ATTR_OTA_TYPE = "9066";
    public final static String ATTR_OTA_UPDATE_STATE = "9054";
    public final static String ATTR_OTA_UPDATE = "9037";

    public final static String ATTR_PUBLIC_KEY = "9098";
    public final static String ATTR_PRIVATE_KEY = "9099";
    public final static String ATTR_PSK = "9091";

    public final static String ATTR_REACHABLE_STATE = "9019";
    public final static String ATTR_REPEAT_DAYS = "9041";

    public final static String ATTR_SEND_CERT_TO_GATEWAY = "9094";
    public final static String ATTR_SEND_COGNITO_ID_TO_GATEWAY = "9095";
    public final static String ATTR_SEND_GH_COGNITO_ID_TO_GATEWAY = "9104";
    public final static String ATTR_SENSOR = "3300";
    public final static String ATTR_SENSOR_MAX_RANGE_VALUE = "5604";
    public final static String ATTR_SENSOR_MAX_MEASURED_VALUE = "5602";
    public final static String ATTR_SENSOR_MIN_RANGE_VALUE = "5603";
    public final static String ATTR_SENSOR_MIN_MEASURED_VALUE = "5601";
    public final static String ATTR_SENSOR_TYPE = "5751";
    public final static String ATTR_SENSOR_UNIT = "5701";
    public final static String ATTR_SENSOR_VALUE = "5700";
    public final static String ATTR_START_ACTION = "9042";  // array
    public final static String ATTR_SMART_TASK_TYPE = "9040";  // 4 = transition | 1 = not home | 2 = on/off
    public final static int ATTR_SMART_TASK_NOT_AT_HOME = 1;
    public final static int ATTR_SMART_TASK_LIGHTS_OFF = 2;
    public final static int ATTR_SMART_TASK_WAKE_UP = 4;
    public final static String ATTR_SMART_TASK_TRIGGER_TIME_INTERVAL = "9044";
    public final static String ATTR_SMART_TASK_TRIGGER_TIME_START_HOUR = "9046";
    public final static String ATTR_SMART_TASK_TRIGGER_TIME_START_MIN = "9047";

    public final static String ATTR_SWITCH_CUM_ACTIVE_POWER = "5805";
    public final static String ATTR_SWITCH_ON_TIME = "5852";
    public final static String ATTR_SWITCH_PLUG = "3312";
    public final static String ATTR_SWITCH_POWER_FACTOR = "5820";

    public final static String ATTR_TRANSITION_TIME = "5712";

    public final static String ATTR_USE_CURRENT_LIGHT_SETTINGS = "9070";

    // URL to json-file containing links to all firmware updates
    public final static String URL_OTA_FW = "http://fw.ota.homesmart.ikea.net/feed/version_info.json";


//    // Mireds range that white-spectrum bulbs can show
//    RANGE_MIREDS = (250, 454)
//
//    // Hue of a RGB bulb
//    RANGE_HUE = (0, 65535)
//    // Effecitive saturation range of a RGB bulb. The bulb will accept
//    // slightly higher values, but it won't produce any light.
//    RANGE_SATURATION = (0, 65279)
//    // Brightness range of all bulbs. 0 will turn off the lamp
//    RANGE_BRIGHTNESS = (0, 254)
//
//    // XY color
//    RANGE_X = (0, 65535)
//    RANGE_Y = (0, 65535)


    public final static int SUPPORT_BRIGHTNESS = 1;
    public final static int SUPPORT_COLOR_TEMP = 2;
    public final static int SUPPORT_HEX_COLOR = 4;
    public final static int SUPPORT_RGB_COLOR = 8;
    public final static int SUPPORT_XY_COLOR = 16;
}
