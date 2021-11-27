var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "12000",
        "ok": "12000",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "6872",
        "ok": "6872",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "1280",
        "ok": "1280",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "1115",
        "ok": "1115",
        "ko": "-"
    },
    "percentiles1": {
        "total": "907",
        "ok": "907",
        "ko": "-"
    },
    "percentiles2": {
        "total": "1979",
        "ok": "1979",
        "ko": "-"
    },
    "percentiles3": {
        "total": "3363",
        "ok": "3363",
        "ko": "-"
    },
    "percentiles4": {
        "total": "4612",
        "ok": "4612",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 5380,
    "percentage": 45
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 1762,
    "percentage": 15
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 4858,
    "percentage": 40
},
    "group4": {
    "name": "failed",
    "count": 0,
    "percentage": 0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "176.471",
        "ok": "176.471",
        "ko": "-"
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
        "total": "12000",
        "ok": "12000",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "1",
        "ok": "1",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "6872",
        "ok": "6872",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "1280",
        "ok": "1280",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "1115",
        "ok": "1115",
        "ko": "-"
    },
    "percentiles1": {
        "total": "907",
        "ok": "907",
        "ko": "-"
    },
    "percentiles2": {
        "total": "1979",
        "ok": "1979",
        "ko": "-"
    },
    "percentiles3": {
        "total": "3363",
        "ok": "3363",
        "ko": "-"
    },
    "percentiles4": {
        "total": "4612",
        "ok": "4612",
        "ko": "-"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 5380,
    "percentage": 45
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 1762,
    "percentage": 15
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 4858,
    "percentage": 40
},
    "group4": {
    "name": "failed",
    "count": 0,
    "percentage": 0
},
    "meanNumberOfRequestsPerSecond": {
        "total": "176.471",
        "ok": "176.471",
        "ko": "-"
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
