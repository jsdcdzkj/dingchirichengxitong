import dayjs from "dayjs";
import { reactive } from "vue";

// 定义各项问题减分值
const demandRule = [3, 1, 1, 1, 5, 1, 1, 1, 3, 1, 3, 1, 4, 5, 1, 5, 1, 1, 2, 3, 1, 1];

// 导出一个类型FinalRateType，包含以下属性：
export type FinalRateType = {
	// 需要全对题目的扣分
	requireFullQuestionDeduct?: number;
	// 需要全对题目的数量
	requireFullQuestionNum?: number;
	// UI设计题目的数量
	uiDesignQuestionNum?: number;
	// UI设计题目的扣分
	uiDesignQuestionDeduct?: number;
	// 用户体验题目的数量
	userExperienceQuestionNum?: number;
	// 用户体验题目的扣分
	userExperienceDeduct?: number;
	// 重大bug数量
	greatNum?: number;
	// 一般bug数量
	normalNum?: number;
	// 轻微bug数量
	slightNum?: number;
	// 验收重大bug分数
	greatPoint?: number;
	// 一般bug分数
	normalPoint?: number;
	// 轻微bug分数
	slightPoint?: number;
	// 最终评分
	reviewTotalPoints: number;
	// 延迟天数
	milestonePostponeDay: number;
	// 延迟评分
	milestoneFinishPoint: number;
	// 功能实现与信息架构完整性问题数量
	functionArchitectureFullQuestionNum?: number;
	// 功能实现与信息架构完整性扣分
	functionArchitectureFullDeduct?: number;
	// 设计缺陷数量
	designDefectNum?: number;
	// 设计缺陷扣分
	designDefectDeduct?: number;
	userExperiencePoint: number;
	clearLogicPoint: number;
	qualityProblemNum: number;
	qualityProblemDeduct: number;
};

export const useRate = (groupType: number) => {
	const rateData = reactive<FinalRateType>({
		// 延期天数
		milestonePostponeDay: 0,
		// 延期评分
		milestoneFinishPoint: 0,
		// 最终评分
		reviewTotalPoints: 0,
		// 需要全对题目的扣分
		requireFullQuestionDeduct: 0,
		// 需要全对题目的数量
		requireFullQuestionNum: 0,
		// UI设计题目的数量
		uiDesignQuestionNum: 0,
		// UI设计题目的扣分
		uiDesignQuestionDeduct: 0,
		// 用户体验题目的数量
		userExperienceQuestionNum: 0,
		// 用户体验题目的扣分
		userExperienceDeduct: 0,
		functionArchitectureFullQuestionNum: 0,
		functionArchitectureFullDeduct: 0,
		designDefectNum: 0,
		designDefectDeduct: 0,
		userExperiencePoint: 5,
		clearLogicPoint: 5,
		qualityProblemNum: 0,
		qualityProblemDeduct: 0
	});
	// 计算 两个时间之间天数的差值
	const getDays = (startDate: string, endDate: string) => {
		const start = new Date(startDate);
		const end = new Date(endDate);
		const diffTime = end.getTime() - start.getTime();
		const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
		return diffDays;
	};

	// 获取延期天数和评分
	const getDelayDaysAndRate = (startDate: string, endDate: string) => {
		const days = getDays(startDate, endDate);
		if (days < 0) {
			return { milestonePostponeDay: days, milestoneFinishPoint: 40 };
		} else if (days == 0) {
			return { milestonePostponeDay: days, milestoneFinishPoint: 30 };
		} else if (days <= 2) {
			return { milestonePostponeDay: days, milestoneFinishPoint: 20 };
		}
		return { milestonePostponeDay: days, milestoneFinishPoint: 0 };
	};

	/**
     *  严重BUG一个扣5分
        一般BUG一个扣2分
        轻微BUG一个扣1分
        出现阻塞性BUG扣50分
     */
	const getBugRate = (bugList: any[]) => {
		let rate = 0;
		bugList.forEach((bug: any) => {
			if (bug.isChoke == 1) {
				rate += 50;
			} else if (bug.severity == 3) {
				// 严重
				rate += 5;
			} else if (bug.severity == 2) {
				// 轻微
				rate += 2;
			} else if (bug.severity == 1) {
				//
				rate += 1;
			}
		});
		return {
			qualityProblemNum: bugList.length,
			qualityProblemDeduct: rate > 50 ? 50 : rate
		};
	};

	// 定义一个函数，用于计算函数列表中的函数类型占比
	const getFunctionRate = (functionList: any[]) => {
		// 定义一个二维数组，用于存储不同问题
		let rate = [
			[0, 0, 8, 14], // 需要全量问题
			[0, 0, 15, 19], // UI设计问题
			[0, 0, 20, 21] // 用户体验问题
		];
		// 遍历函数列表
		functionList.forEach((functionItem: any) => {
			// 获取函数类型
			const index = Number(functionItem.question_type) - 1;
			// 判断函数类型是否在需要全量问题的范围内
			if (index >= rate[0][2] && index <= rate[0][3]) {
				// 减去对应类型的扣分规则
				rate[0][1] += demandRule[index];
				// 增加需要全量问题的数量
				rate[0][0] += 1;
				// 判断函数类型是否在UI设计问题的范围内
			} else if (index >= rate[1][2] && index <= rate[1][3]) {
				// 减去对应类型的扣分规则
				rate[1][1] += demandRule[index];
				// 增加UI设计问题的数量
				rate[1][0] += 1;
				// 判断函数类型是否在用户体验问题的范围内
			} else if (index >= rate[2][2] && index <= rate[2][3]) {
				// 减去对应类型的扣分规则
				rate[2][1] += demandRule[index];
				// 增加用户体验问题的数量
				rate[2][0] += 1;
			}
		});
		// 返回计算结果
		return {
			requireFullQuestionDeduct: rate[0][1] > 30 ? 30 : rate[0][1], // 需要全量问题的扣分
			requireFullQuestionNum: rate[0][0], // 需要全量问题的数量
			uiDesignQuestionNum: rate[1][0], // UI设计问题的数量
			uiDesignQuestionDeduct: rate[1][1] > 20 ? 20 : rate[1][1], // UI设计问题的扣分
			userExperienceQuestionNum: rate[2][0], // 用户体验问题的数量
			userExperienceDeduct: rate[2][1] > 10 ? 10 : rate[2][1] // 用户体验问题的扣分
		};
	};

	// 定义一个函数，用于计算函数列表中的函数类型占比
	const getFunctionDesignRate = (functionList: any[]) => {
		// 定义一个二维数组，用于存储不同问题
		let rate = [
			[0, 0, 0, 3],
			[0, 0, 4, 7]
		];
		// 遍历函数列表
		functionList.forEach((functionItem: any) => {
			// 获取函数类型
			const index = Number(functionItem.question_type) - 1;
			// 判断函数类型是否在需要全量问题的范围内
			if (index >= rate[0][2] && index <= rate[0][3]) {
				// 减去对应类型的扣分规则
				rate[0][1] += demandRule[index];
				// 增加需要全量问题的数量
				rate[0][0] += 1;
				// 判断函数类型是否在UI设计问题的范围内
			} else if (index >= rate[1][2] && index <= rate[1][3]) {
				// 减去对应类型的扣分规则
				rate[1][1] += demandRule[index];
				// 增加UI设计问题的数量
				rate[1][0] += 1;
				// 判断函数类型是否在用户体验问题的范围内
			}
		});
		// 返回计算结果
		return {
			functionArchitectureFullDeduct: rate[0][1] > 30 ? 30 : rate[0][1],
			functionArchitectureFullQuestionNum: rate[0][0],
			designDefectNum: rate[1][0],
			designDefectDeduct: rate[1][1] > 30 ? 30 : rate[1][1]
		};
	};

	const getFinalRate = (appointFinishTime: string, bugList: any[]) => {
		const delay = getDelayDaysAndRate(appointFinishTime, dayjs().format("YYYY-MM-DD"));
		let functionRate: any;
		let reviewTotalPoints = 100;
		if (groupType == 1) {
			// 原型评审阶段
			functionRate = getFunctionRate(bugList);
			reviewTotalPoints =
				delay.milestoneFinishPoint +
				60 -
				(functionRate.requireFullQuestionDeduct +
					functionRate.uiDesignQuestionDeduct +
					functionRate.userExperienceDeduct);
		} else if (groupType == 2) {
			//  设计阶段评审
			functionRate = getFunctionDesignRate(bugList);
			reviewTotalPoints =
				delay.milestoneFinishPoint +
				60 -
				(functionRate.functionArchitectureFullDeduct + functionRate.designDefectDeduct);
		} else {
			// 其他阶段
			functionRate = getBugRate(bugList);
			reviewTotalPoints =
				delay.milestoneFinishPoint +
				rateData.userExperiencePoint +
				rateData.clearLogicPoint +
				50 -
				functionRate.qualityProblemDeduct;
		}

		const _rateObj = {
			...functionRate,
			reviewTotalPoints,
			...delay
		};

		Reflect.ownKeys(_rateObj).forEach((key) => {
			Reflect.set(rateData, key, Reflect.get(_rateObj, key));
		});
	};

	return { getFinalRate, rateData };
};
