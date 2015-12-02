package flowcontrol

def Map<String,List<String>> evalAddress(String serviceID,List<String> allAddress, List<String> unitAddress)  {
    //
    //[RSF]sorg.mytest.FooFacse-1.0.0 ---- Group=RSF, Name=sorg.mytest.FooFacse, Version=1.0.0
    if ( serviceID == "[RSF]sorg.mytest.FooFacse-1.0.0" ) {
        return [
            "println":[
                "192.168.1.2:8000",
                "192.168.1.2:8001",
                "192.168.1.3:8000"
            ],
            "sayEcho":[
                "192.168.1.2:8000",
            ],
            "testUserTag":[
                "192.168.1.2:8000",
                "192.168.1.3:8000"
            ]
        ]
    }
    return null
}