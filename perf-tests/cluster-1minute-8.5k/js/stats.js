var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "8500",
        "ok": "8433",
        "ko": "67"
    },
    "minResponseTime": {
        "total": "2",
        "ok": "2",
        "ko": "2170"
    },
    "maxResponseTime": {
        "total": "5778",
        "ok": "5778",
        "ko": "5433"
    },
    "meanResponseTime": {
        "total": "1284",
        "ok": "1264",
        "ko": "3761"
    },
    "standardDeviation": {
        "total": "1014",
        "ok": "990",
        "ko": "969"
    },
    "percentiles1": {
        "total": "951",
        "ok": "944",
        "ko": "3751"
    },
    "percentiles2": {
        "total": "1994",
        "ok": "1958",
        "ko": "4554"
    },
    "percentiles3": {
        "total": "3148",
        "ok": "3096",
        "ko": "5342"
    },
    "percentiles4": {
        "total": "4297",
        "ok": "4119",
        "ko": "5430"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 3570,
    "percentage": 42
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 1385,
    "percentage": 16
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 3478,
    "percentage": 41
},
    "group4": {
    "name": "failed",
    "count": 67,
    "percentage": 1
},
    "meanNumberOfRequestsPerSecond": {
        "total": "123.188",
        "ok": "122.217",
        "ko": "0.971"
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
        "total": "8500",
        "ok": "8433",
        "ko": "67"
    },
    "minResponseTime": {
        "total": "2",
        "ok": "2",
        "ko": "2170"
    },
    "maxResponseTime": {
        "total": "5778",
        "ok": "5778",
        "ko": "5433"
    },
    "meanResponseTime": {
        "total": "1284",
        "ok": "1264",
        "ko": "3761"
    },
    "standardDeviation": {
        "total": "1014",
        "ok": "990",
        "ko": "969"
    },
    "percentiles1": {
        "total": "951",
        "ok": "944",
        "ko": "3751"
    },
    "percentiles2": {
        "total": "1994",
        "ok": "1958",
        "ko": "4554"
    },
    "percentiles3": {
        "total": "3148",
        "ok": "3096",
        "ko": "5342"
    },
    "percentiles4": {
        "total": "4297",
        "ok": "4119",
        "ko": "5430"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 3570,
    "percentage": 42
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 1385,
    "percentage": 16
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 3478,
    "percentage": 41
},
    "group4": {
    "name": "failed",
    "count": 67,
    "percentage": 1
},
    "meanNumberOfRequestsPerSecond": {
        "total": "123.188",
        "ok": "122.217",
        "ko": "0.971"
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
