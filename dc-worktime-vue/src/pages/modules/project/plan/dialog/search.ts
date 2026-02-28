import { projectUserList } from "@/api/project/list";
import { appInstance } from "@/main";
import dayjs from "dayjs";
import { reactive } from "vue";

export const usePlanSearch = () => {
	const searchFrom = reactive({
		type: 1,
		timeType: -1,
		plan_title: "",
		person_in_charge: "",
		time: [] as string[],
		userList: [] as any[],
		showPopover: false
	});

	const getUseList = async () => {
		const data = await projectUserList({
			projectId: appInstance.projectItem.id
		});
		searchFrom.userList = data;
	};

	const handleTimeChange = (type: number) => {
		// 本日 dayjs
		if (type == 1) {
			searchFrom.time = [dayjs().startOf("day").format("YYYY-MM-DD"), dayjs().endOf("day").format("YYYY-MM-DD")];
		}
		// 本周
		else if (type == 2) {
			searchFrom.time = [
				dayjs().startOf("week").format("YYYY-MM-DD"),
				dayjs().endOf("week").format("YYYY-MM-DD")
			];
		}
		// 本月
		else if (type == 3) {
			searchFrom.time = [
				dayjs().startOf("month").format("YYYY-MM-DD"),
				dayjs().endOf("month").format("YYYY-MM-DD")
			];
		}
		return type != 4;
	};

	const handleSearchFormReset = () => {
		searchFrom.plan_title = "";
		searchFrom.person_in_charge = "";
		searchFrom.time = [] as string[];
		searchFrom.timeType = -1;
	};

	const getSearchData = () => {
		const { plan_title, person_in_charge } = searchFrom;
		const query_start_date = searchFrom.time[0];
		const query_end_date = searchFrom.time[1];
		return {
			query_start_date: query_start_date ? query_start_date : "",
			query_end_date: query_end_date ? query_end_date : "",
			plan_title,
			person_in_charge,
			type: searchFrom.type
		};
	};
	getUseList();

	return { searchFrom, handleTimeChange, getSearchData, handleSearchFormReset };
};
