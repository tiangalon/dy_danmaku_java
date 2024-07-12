var arguments = process.argv;
var room_id, user_unique_id;
arguments.forEach((val, index) => {
    //console.log(`${index}: ${val}`);
    if(index == 2){
        room_id = val;
        //console.log(room_id);
    }
    if(index == 3){
        user_unique_id = val;
        //console.log(user_unique_id);
    }
});

if(!room_id ||!user_unique_id){
    console.log('缺少参数');
    process.exit();
}

const userAgent = 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36';
const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const dom = new JSDOM('<!DOCUMENT html><p>Test</p>')
window = dom.window
document = window.document
navigator= window.navigator

var runtime = require('./runtime~client-entry.44b556b4.js');
var webmssdk = require('./webmssdk.es5.js');
const { sign } = require("crypto");

window.toString = function() {return "[object Window]"};
var base = `live_id=1,aid=6383,version_code=180800,webcast_sdk_version=1.0.14-beta.0,room_id=${room_id},sub_room_id=,sub_channel_id=,did_rule=3,user_unique_id=${user_unique_id},device_platform=web,device_type=,ac=,identity=audience`;
var t = window._g_(99637),
    i = window._g_(73963).utf8,
    n = window._g_(20850),
    o = window._g_(73963).bin
var X_MS_STUB = window._g_(14570)(base);
const s_X_MS_STUB = {'X-MS-STUB' : X_MS_STUB};
var X_Bogus = window._0x5c2014(s_X_MS_STUB);
var signature = X_Bogus["X-Bogus"];
console.log(signature);
process.exit();