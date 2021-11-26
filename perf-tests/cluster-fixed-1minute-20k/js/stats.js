var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "20000",
        "ok": "18551",
        "ko": "1449"
    },
    "minResponseTime": {
        "total": "3",
        "ok": "3",
        "ko": "27721"
    },
    "maxResponseTime": {
        "total": "64771",
        "ok": "60141",
        "ko": "64771"
    },
    "meanResponseTime": {
        "total": "20139",
        "ok": "18289",
        "ko": "43831"
    },
    "standardDeviation": {
        "total": "11702",
        "ok": "9828",
        "ko": "6953"
    },
    "percentiles1": {
        "total": "18656",
        "ok": "17612",
        "ko": "43302"
    },
    "percentiles2": {
        "total": "24657",
        "ok": "22880",
        "ko": "49313"
    },
    "percentiles3": {
        "total": "42809",
        "ok": "37285",
        "ko": "55117"
    },
    "percentiles4": {
        "total": "53004",
        "ok": "44278",
        "ko": "59011"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 262,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 91,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 18198,
    "percentage": 91
},
    "group4": {
    "name": "failed",
    "count": 1449,
    "percentage": 7
},
    "meanNumberOfRequestsPerSecond": {
        "total": "137.931",
        "ok": "127.938",
        "ko": "9.993"
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
        "ok": "18551",
        "ko": "1449"
    },
    "minResponseTime": {
        "total": "3",
        "ok": "3",
        "ko": "27721"
    },
    "maxResponseTime": {
        "total": "64771",
        "ok": "60141",
        "ko": "64771"
    },
    "meanResponseTime": {
        "total": "20139",
        "ok": "18289",
        "ko": "43831"
    },
    "standardDeviation": {
        "total": "11702",
        "ok": "9828",
        "ko": "6953"
    },
    "percentiles1": {
        "total": "18669",
        "ok": "17614",
        "ko": "43302"
    },
    "percentiles2": {
        "total": "24650",
        "ok": "22880",
        "ko": "49313"
    },
    "percentiles3": {
        "total": "42810",
        "ok": "37279",
        "ko": "55117"
    },
    "percentiles4": {
        "total": "53004",
        "ok": "44278",
        "ko": "59011"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 262,
    "percentage": 1
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 91,
    "percentage": 0
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 18198,
    "percentage": 91
},
    "group4": {
    "name": "failed",
    "count": 1449,
    "percentage": 7
},
    "meanNumberOfRequestsPerSecond": {
        "total": "137.931",
        "ok": "127.938",
        "ko": "9.993"
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
