import { createModelAsync } from "@/core/dialog";
import AddVersion from "./add-version.vue";
import { ElMessageBox } from "element-plus";
import DeleteVersion from "./delete-version.vue";
import { deleteIteration, getIterationList } from "@/api/project/plan";
import { computed, onUnmounted, reactive, ref } from "vue";
import { appInstance } from "@/main";

export type IterationType = {
	iteration_name: string;
	id: number;
};

export type Iteration = {
	list: IterationType[];
	currentId: number | string;
};

export const SKey = "iteration_id";

export const useVersion = (showAll?: boolean) => {
	const iteration = reactive<Iteration>({
		list: [],
		currentId: localStorage.getItem(SKey) ? Number(localStorage.getItem(SKey)) : ""
	});

	const refreshVersionList = async () => {
		iteration.list = await getIterationList({ project_id: appInstance.projectItem.id });
		if (iteration.list.length > 0) {
			iteration.currentId = iteration.list.slice(-1)[0].id;
			setSKey(iteration.currentId as number);
		} else {
			iteration.currentId = "";
			setSKey("");
		}
	};

	// 添加迭代
	const handleAddVersion = async () => {
		await createModelAsync({ title: "新增迭代版本", width: "500px" }, {}, <AddVersion />);
		await refreshVersionList();
	};

	const handleEditVersion = () => {
		createModelAsync(
			{ title: "编辑迭代版本", width: "500px" },
			{},
			<AddVersion item={iteration.list.find((item) => item.id == iteration.currentId)} />
		).then(async () => {
			iteration.list = await getIterationList({ project_id: appInstance.projectItem.id });
		});
	};

	const disabledVersion = computed(() => {
		return iteration.currentId == "";
	});

	const setSKey = (value: number | string) => {
		if (value == -1) return;
		localStorage.setItem(SKey, value.toString());
	};

	// 删除迭代
	const handleDeleteVersion = async () => {
		await createModelAsync(
			{ title: "删除迭代版本", width: "800px" },
			{},
			<DeleteVersion id={iteration.currentId as number} />
		);
		await refreshVersionList();
	};

	const getVersionList = async () => {
		const list = await getIterationList({ project_id: appInstance.projectItem.id });
		if (showAll) {
			iteration.list = [
				{
					id: -1,
					project_id: 1,
					iteration_name: "全部",
					person_in_charge: 297,
					person_in_charge_name: "全部",
					is_template: 0,
					description: "全部",
					createUser: null,
					createUserName: null,
					updateUser: 297,
					createTime: "2025-04-21 18:35:56",
					updateTime: "2025-04-21 18:37:21",
					isDel: 0
				},
				...list
			];
		} else {
			iteration.list = list;
		}

		if (iteration.list.length > 0 && iteration.currentId == "") {
			iteration.currentId = iteration.list[0].id;
			setSKey(iteration.currentId as number);
		}
	};

	const getIterationId = () => {
		if (iteration.currentId == -1) {
			return { id: null };
		}
		return {
			id: iteration.currentId
		};
	};

	return {
		handleDeleteVersion,
		handleAddVersion,
		getVersionList,
		iteration,
		setSKey,
		disabledVersion,
		handleEditVersion,
		getIterationId
	};
};
