export const getOptions = ({ requirement, task, bug }) => {
	var xData = ["需求", "任务", "bug"];
	var data1 = [requirement[0].notStart, task[0].notStart, bug[0].notStart];
	var data2 = [requirement[0].underway, task[0].underway, bug[0].underway];
	var data4 = [requirement[0].finish, task[0].finish, bug[0].finish];

	var data3 = [];
	for (let i = 0; i < data1.length; i++) {
		data3.push(data1[i] + data2[i]);
	}
	var data5 = [];
	for (let i = 0; i < data1.length; i++) {
		data5.push(data3[i] + data4[i]);
	}

	var option = {
		tooltip: {
			trigger: "axis",
			axisPointer: {
				type: "shadow" // 默认为直线，可选为：'line' | 'shadow'
			},

			formatter: (parms) => {
				let list = [];
				const index = parms[0].dataIndex;
				list.push(data1[index]);
				list.push(data2[index]);
				list.push(data4[index]);

				if (index == 0) {
					list.push(requirement[0].totalSize);
				} else if (index == 1) {
					// total = task[0].totalSize;
					list.push(task[0].totalSize);
				} else if (index == 2) {
					// total = bug[0].totalSize;
					list.push(bug[0].totalSize);
				}

				// console.log(requirement);

				var str =
					"名称：" +
					parms[0].axisValue +
					"</br>" +
					parms[0].marker +
					"未开始 " +
					list[0] +
					"</br>" +
					parms[1].marker +
					"进行中 " +
					list[1] +
					"</br>" +
					parms[2].marker +
					"已结束 " +
					list[2] +
					"</br>" +
					parms[3].marker +
					"全部&nbsp;&nbsp;&nbsp;" +
					list[3];
				return str;
			}
		},
		textStyle: {
			color: "#C9C9C9"
		},
		color: ["#7BA9FA", "#7BA9FA", "#DE9FB1", "#DE9FB1"],
		grid: {
			containLabel: true,
			left: "0%",
			top: "5%",
			bottom: "0%",
			right: "0%"
		},
		legend: {
			top: "0",
			left: "center"
		},
		xAxis: {
			type: "category",
			data: xData,
			axisLine: {
				lineStyle: {
					color: "#B5B5B5"
				}
			},
			axisTick: {
				show: false
			},
			axisLabel: {
				margin: 20, //刻度标签与轴线之间的距离。
				textStyle: {
					fontFamily: "Microsoft YaHei",
					color: "#C9C9C9"
				}
			}
		},
		yAxis: {
			type: "value",
			axisLine: {
				show: true,
				lineStyle: {
					color: "#B5B5B5"
				}
			},
			splitLine: {
				lineStyle: {
					// 使用深浅的间隔色
					color: ["#B5B5B5"],
					type: "dashed",
					opacity: 0.5
				}
			},
			axisLabel: {}
		},
		series: [
			{
				data: data1,
				stack: "zs",
				type: "bar",
				barMaxWidth: "auto",
				barWidth: 60,
				itemStyle: {
					color: {
						x: 0,
						y: 0,
						x2: 0,
						y2: 1,
						type: "linear",
						global: false,
						colorStops: [
							{
								offset: 0,
								color: "#5EA1FF"
							},
							{
								offset: 1,
								color: "#90BEFF"
							}
						]
					}
				}
			},
			{
				data: data2,
				stack: "zs",
				type: "bar",
				barMaxWidth: "auto",
				barWidth: 60,
				itemStyle: {
					color: {
						x: 0,
						y: 0,
						x2: 0,
						y2: 1,
						type: "linear",
						global: false,
						colorStops: [
							{
								offset: 0,
								color: "#FF6772"
							},
							{
								offset: 1,
								color: "#FFABAC"
							}
						]
					}
				}
			},
			{
				data: data4,
				stack: "zs",
				type: "bar",
				barMaxWidth: "auto",
				barWidth: 60,
				itemStyle: {
					color: {
						x: 0,
						y: 0,
						x2: 0,
						y2: 1,
						type: "linear",
						global: false,
						colorStops: [
							{
								offset: 0,
								color: "#5EA1FF"
							},
							{
								offset: 1,
								color: "#90BEFF"
							}
						]
					}
				}
			},
			{
				data: [1, 1, 1, 1, 1, 1, 1],
				type: "pictorialBar",
				barMaxWidth: "20",
				symbol: "diamond",
				symbolOffset: [0, "50%"],
				symbolSize: [60, 15],
				zlevel: 2
			},
			{
				data: data1,
				type: "pictorialBar",
				barMaxWidth: "20",
				symbolPosition: "end",
				symbol: "diamond",
				symbolOffset: [0, "-50%"],
				symbolSize: [60, 12],
				zlevel: 2
			},
			{
				data: data1,
				type: "pictorialBar",
				barMaxWidth: "20",
				symbolPosition: "end",
				symbol: "diamond",
				symbolOffset: [0, "-50%"],
				symbolSize: [60, 12],
				zlevel: 2
			},
			{
				data: data1,
				type: "pictorialBar",
				barMaxWidth: "20",
				symbolPosition: "end",
				symbol: "diamond",
				symbolOffset: [0, "-50%"],
				symbolSize: [60, 12],
				zlevel: 2
			},

			{
				data: data3,
				type: "pictorialBar",
				barMaxWidth: "20",
				symbolPosition: "end",
				symbol: "diamond",
				symbolOffset: [0, "-50%"],
				symbolSize: [60, 12],
				zlevel: 2
			},
			{
				data: data5,
				type: "pictorialBar",
				barMaxWidth: "20",
				symbolPosition: "end",
				symbol: "diamond",
				symbolOffset: [0, "-50%"],
				symbolSize: [60, 12],
				zlevel: 2
			}
		]
	};

	// var current = -1;
	// var dataLen1 = option.series[0].data.length;
	// setInterval(() => {
	//     // 取消之前高亮的图形
	//     myChart.dispatchAction({
	//         type: 'downplay',
	//         seriesIndex: 0,
	//         dataIndex: current,
	//     });
	//     current = (current + 1) % dataLen1;
	//     // 高亮当前图形
	//     myChart.dispatchAction({
	//         type: 'highlight',
	//         seriesIndex: 0,
	//         dataIndex: current,
	//     });
	//     // 显示 tooltip
	//     myChart.dispatchAction({
	//         type: 'showTip',
	//         seriesIndex: 0,
	//         dataIndex: current,
	//     });
	// }, 2000);
	return option;
};
