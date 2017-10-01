define("welcome", ["jquery", "chart"], function ($, Chart) {
    $(function () {
        $.ajax({
            url: getComputedCountUrl,
            type: "get",
            data: {
                "_token": _token
            },
            dataType: "json",
            success: function(data) {
                if (data) {
                    var pieChat = new Chart(document.getElementById("pie_chart").getContext("2d"), {
                        type: "pie",
                        data: data.pieChart
                    });
                    var barChart = new Chart(document.getElementById("bar_chart").getContext("2d"), {
                        type: "bar",
                        data: data.barChart,
                        options: {
                            events: ['click'],
                            onClick: function (evt) {

                            }
                        }
                    });
                    $("#actual_number").html(data.tipData.actualNumber);
                    $("#database_number").html(data.tipData.databaseNumber);
                    $("#lack_number").html(data.tipData.actualNumber - data.tipData.databaseNumber);
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
            },
            complete: function(XMLHttpRequest, textStatus) {
            }
        });
        /**/
    });
});
