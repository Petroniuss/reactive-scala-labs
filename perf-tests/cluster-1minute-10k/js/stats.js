var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "10000",
        "ok": "9057",
        "ko": "943"
    },
    "minResponseTime": {
        "total": "2",
        "ok": "2",
        "ko": "470"
    },
    "maxResponseTime": {
        "total": "13646",
        "ok": "8907",
        "ko": "13646"
    },
    "meanResponseTime": {
        "total": "2134",
        "ok": "1600",
        "ko": "7267"
    },
    "standardDeviation": {
        "total": "2698",
        "ok": "2134",
        "ko": "2094"
    },
    "percentiles1": {
        "total": "507",
        "ok": "292",
        "ko": "7338"
    },
    "percentiles2": {
        "total": "3826",
        "ok": "2778",
        "ko": "8690"
    },
    "percentiles3": {
        "total": "7509",
        "ok": "6287",
        "ko": "10589"
    },
    "percentiles4": {
        "total": "9866",
        "ok": "7176",
        "ko": "11956"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 5252,
    "percentage": 53
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 188,
    "percentage": 2
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 3617,
    "percentage": 36
},
    "group4": {
    "name": "failed",
    "count": 943,
    "percentage": 9
},
    "meanNumberOfRequestsPerSecond": {
        "total": "133.333",
        "ok": "120.76",
        "ko": "12.573"
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
        "total": "10000",
        "ok": "9057",
        "ko": "943"
    },
    "minResponseTime": {
        "total": "2",
        "ok": "2",
        "ko": "470"
    },
    "maxResponseTime": {
        "total": "13646",
        "ok": "8907",
        "ko": "13646"
    },
    "meanResponseTime": {
        "total": "2134",
        "ok": "1600",
        "ko": "7267"
    },
    "standardDeviation": {
        "total": "2698",
        "ok": "2134",
        "ko": "2094"
    },
    "percentiles1": {
        "total": "506",
        "ok": "292",
        "ko": "7338"
    },
    "percentiles2": {
        "total": "3826",
        "ok": "2778",
        "ko": "8690"
    },
    "percentiles3": {
        "total": "7509",
        "ok": "6287",
        "ko": "10589"
    },
    "percentiles4": {
        "total": "9866",
        "ok": "7177",
        "ko": "11956"
    },
    "group1": {
    "name": "t < 800 ms",
    "count": 5252,
    "percentage": 53
},
    "group2": {
    "name": "800 ms < t < 1200 ms",
    "count": 188,
    "percentage": 2
},
    "group3": {
    "name": "t > 1200 ms",
    "count": 3617,
    "percentage": 36
},
    "group4": {
    "name": "failed",
    "count": 943,
    "percentage": 9
},
    "meanNumberOfRequestsPerSecond": {
        "total": "133.333",
        "ok": "120.76",
        "ko": "12.573"
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
