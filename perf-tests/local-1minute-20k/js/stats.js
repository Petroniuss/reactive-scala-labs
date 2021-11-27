var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "20000",
        "ok": "4904",
        "ko": "15096"
    },
    "minResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "119"
    },
    "maxResponseTime": {
        "total": "87534",
        "ok": "57600",
        "ko": "87534"
    },
    "meanResponseTime": {
        "total": "43686",
        "ok": "21049",
        "ko": "51039"
    },
    "standardDeviation": {
        "total": "19015",
        "ok": "13594",
        "ko": "14088"
    },
    "percentiles1": {
        "total": "45816",
        "ok": "22587",
        "ko": "53784"
    },
    "percentiles2": {
        "total": "60013",
        "ok": "30375",
        "ko": "60320"
    },
    "percentiles3": {
        "total": "68339",
        "ok": "45337",
        "ko": "69659"
    },
    "percentiles4": {
        "total": "75399",
        "ok": "50118",
        "ko": "76977"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 282,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 68,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 4554,
    "percentage": 23
},
    "group4": {
    "name": "failed",
    "count": 15096,
    "percentage": 75
},
    "meanNumberOfRequestsPerSecond": {
        "total": "119.048",
        "ok": "29.19",
        "ko": "89.857"
    }
},
contents: {
"req_search-06a94": {
        type: "REQUEST",
        name: "search",
path: "search",
pathFormatted: "req_search-06a94",
stats: {
    "name": "search",
    "numberOfRequests": {
        "total": "20000",
        "ok": "4904",
        "ko": "15096"
    },
    "minResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "119"
    },
    "maxResponseTime": {
        "total": "87534",
        "ok": "57600",
        "ko": "87534"
    },
    "meanResponseTime": {
        "total": "43686",
        "ok": "21049",
        "ko": "51039"
    },
    "standardDeviation": {
        "total": "19015",
        "ok": "13594",
        "ko": "14088"
    },
    "percentiles1": {
        "total": "45816",
        "ok": "22587",
        "ko": "53784"
    },
    "percentiles2": {
        "total": "60013",
        "ok": "30375",
        "ko": "60320"
    },
    "percentiles3": {
        "total": "68339",
        "ok": "45309",
        "ko": "69659"
    },
    "percentiles4": {
        "total": "75399",
        "ok": "50118",
        "ko": "76977"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 282,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 68,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 4554,
    "percentage": 23
},
    "group4": {
    "name": "failed",
    "count": 15096,
    "percentage": 75
},
    "meanNumberOfRequestsPerSecond": {
        "total": "119.048",
        "ok": "29.19",
        "ko": "89.857"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
