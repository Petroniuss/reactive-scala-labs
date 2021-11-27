var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "20000",
        "ok": "13970",
        "ko": "6030"
    },
    "minResponseTime": {
        "total": "4",
        "ok": "4",
        "ko": "7669"
    },
    "maxResponseTime": {
        "total": "49977",
        "ok": "39370",
        "ko": "49977"
    },
    "meanResponseTime": {
        "total": "11915",
        "ok": "8227",
        "ko": "20459"
    },
    "standardDeviation": {
        "total": "7530",
        "ok": "4478",
        "ko": "6091"
    },
    "percentiles1": {
        "total": "10130",
        "ok": "7626",
        "ko": "19746"
    },
    "percentiles2": {
        "total": "16627",
        "ok": "10400",
        "ko": "22289"
    },
    "percentiles3": {
        "total": "24475",
        "ok": "15293",
        "ko": "36219"
    },
    "percentiles4": {
        "total": "37890",
        "ok": "27615",
        "ko": "41526"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 244,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 58,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 13668,
    "percentage": 68
},
    "group4": {
    "name": "failed",
    "count": 6030,
    "percentage": 30
},
    "meanNumberOfRequestsPerSecond": {
        "total": "186.916",
        "ok": "130.561",
        "ko": "56.355"
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
        "ok": "13970",
        "ko": "6030"
    },
    "minResponseTime": {
        "total": "4",
        "ok": "4",
        "ko": "7669"
    },
    "maxResponseTime": {
        "total": "49977",
        "ok": "39370",
        "ko": "49977"
    },
    "meanResponseTime": {
        "total": "11915",
        "ok": "8227",
        "ko": "20459"
    },
    "standardDeviation": {
        "total": "7530",
        "ok": "4478",
        "ko": "6091"
    },
    "percentiles1": {
        "total": "10129",
        "ok": "7628",
        "ko": "19746"
    },
    "percentiles2": {
        "total": "16627",
        "ok": "10400",
        "ko": "22289"
    },
    "percentiles3": {
        "total": "24476",
        "ok": "15293",
        "ko": "36219"
    },
    "percentiles4": {
        "total": "37890",
        "ok": "27615",
        "ko": "41526"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 244,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 58,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 13668,
    "percentage": 68
},
    "group4": {
    "name": "failed",
    "count": 6030,
    "percentage": 30
},
    "meanNumberOfRequestsPerSecond": {
        "total": "186.916",
        "ok": "130.561",
        "ko": "56.355"
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
