export const getPlanList = () => {
	return [
		{
			id: Math.random().toString(16).slice(2),
			title: "项目立项1",
			planType: 1,
			progress: Math.floor(Math.random() * 100),
			manger: "王响响",
			plan_start_date: "2025-01-13",
			plan_end_date: "2025-01-35",
			status: "进行中",
			children: [
				{
					title: "工作任务",
					id: Math.random().toString(16).slice(2),
					planType: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				},
				{
					id: Math.random().toString(16).slice(2),
					title: "工作任务2",
					type: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				},
				{
					id: Math.random().toString(16).slice(2),
					title: "工作任3",
					type: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				}
			]
		},
		{
			id: Math.random().toString(16).slice(2),
			title: "项目立项2",
			type: 1,
			progress: Math.floor(Math.random() * 100),
			manger: "王响响",
			plan_start_date: "2025-01-13",
			plan_end_date: "2025-01-35",
			status: "进行中",
			children: [
				{
					id: Math.random().toString(16).slice(2),
					title: "工作任1",
					type: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				},
				{
					id: Math.random().toString(16).slice(2),
					title: "工作任2",
					type: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				},
				{
					id: Math.random().toString(16).slice(2),
					title: "工作任3",
					type: 2,
					progress: Math.floor(Math.random() * 100),
					manger: "王响响",
					plan_start_date: "2025-01-13",
					plan_end_date: "2025-01-35",
					status: "进行中"
				}
			]
		}
	];
};
