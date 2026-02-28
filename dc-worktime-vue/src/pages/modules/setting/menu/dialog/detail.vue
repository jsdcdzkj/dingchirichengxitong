<template>
	<ElForm ref="formInstance" :model="form" :rules="rules" label-width="100px">
		<ElFormItem label="菜单名称" prop="title">
			<ElInput v-model="form.title" placeholder="请输入菜单名称" />
		</ElFormItem>
		<!-- 父级菜单 el-tree-select -->
		<ElFormItem label="父级菜单" prop="parentId">
			<ElTreeSelect
				v-model="form.parentId"
				placeholder="请选择父级菜单"
				:data="treeData"
				:props="treeProps"
				filterable
				:filter-node-method="filterNode"
				clearable
				:check-strictly="true"
				:render-after-expand="false"
			/>
		</ElFormItem>
		<!-- 单类型 1:菜单 2:按钮 -->
		<ElFormItem label="菜单类型" prop="menuType">
			<ElSelect v-model="form.menuType" placeholder="请选择菜单类型">
				<template v-for="option in menuTypeOptions" :key="option.value">
					<ElOption :label="option.label" :value="option.value"></ElOption>
				</template>
			</ElSelect>
		</ElFormItem>
		<!-- 按钮标识 -->
		<ElFormItem label="按钮标识" prop="buttonSign" v-if="form.menuType == 2">
			<ElInput v-model="form.buttonSign" placeholder="请输入按钮标识" />
		</ElFormItem>
		<!-- 路由名称 -->
		<ElFormItem label="路由名称" prop="routerName" v-if="form.menuType == 1">
			<ElInput v-model="form.routerName" placeholder="请输入路由名称" />
		</ElFormItem>
		<!-- 路由链接 -->
		<ElFormItem label="路由链接" prop="routerUrl" v-if="form.menuType == 1">
			<ElInput v-model="form.routerUrl" placeholder="请输入路由链接" />
		</ElFormItem>
		<!-- vue文件路径 -->
		<ElFormItem label="vue文件路径" prop="vueUrl" v-if="form.menuType == 1">
			<ElInput v-model="form.vueUrl" placeholder="请输入vue文件路径" />
		</ElFormItem>
		<!-- 重定向类型 -->
		<ElFormItem label="重定向类型" prop="redirectType" v-if="form.menuType == 1">
			<ElInput v-model="form.redirectType" placeholder="请输入重定向类型" />
		</ElFormItem>
		<!-- 图标 -->
		<ElFormItem label="图标" prop="icon" v-if="form.menuType == 1">
			<IconSelect v-model:model-value="form.icon" />
		</ElFormItem>
		<!-- 是否显示 0：不显示 1：显示 -->
		<ElFormItem label="是否显示" prop="isShow">
			<!-- 使用radio -->
			<ElRadioGroup v-model="form.isShow">
				<ElRadio :value="1">是</ElRadio>
				<ElRadio :value="0">否</ElRadio>
			</ElRadioGroup>
		</ElFormItem>
		<!-- 排序 -->
		<ElFormItem label="排序" prop="sort">
			<ElInput v-model="form.sort" placeholder="请输入排序" />
		</ElFormItem>
	</ElForm>
</template>

<script setup lang="ts">
	import { ref, onMounted, reactive } from "vue";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import {
		ElForm,
		ElFormItem,
		ElInput,
		ElTree,
		ElTreeSelect,
		ElRadioGroup,
		ElRadio,
		ElSelect,
		ElOption
	} from "element-plus";

	import { number } from "vue-types";
	import { toRaw } from "vue";
	import { getMenuTree, saveMenu } from "@/api/setting/menu";
	import IconSelect from "@/components/icon/IconSelect";

	const form = reactive({
		title: "",
		parentId: "",
		routerName: "",
		routerUrl: "",
		vueUrl: "",
		redirectType: "",
		icon: "",
		isShow: 1,
		sort: "",
		menuType: 1,
		buttonSign: ""
	});
	const menuTypeOptions = [
		{ label: "菜单", value: 1 },
		{ label: "按钮", value: 2 }
	];

	const props = defineProps({
		// id: number().def()
		rowInfo: {
			type: Object,
			default: () => ({})
		}
	});

	const rules = reactive({
		title: [
			{
				required: true,
				message: "请输入菜单名称",
				trigger: "blur"
			}
		]
	});

	const { registerFormDone, formInstance } = useDialogForm();
	const tree = ref<InstanceType<typeof ElTree>>();

	const treeData = ref<any[]>([]);
	const treeProps = {
		children: "children",
		label: "title",
		value: "id"
	};
	const filterNode = (value: string, data: any) => {
		if (!value) return true;
		// 检查当前节点的 title 是否包含搜索值
		if (data.title && data.title.includes(value)) {
			return true;
		}
		// 递归检查子节点
		if (data.children && data.children.length > 0) {
			for (let i = 0; i < data.children.length; i++) {
				if (filterNode(value, data.children[i])) {
					return true;
				}
			}
		}
		return false;
	};
	if (props.rowInfo.id) {
		for (const key in form) {
			// 修改部分
			if (key in form && key in props.rowInfo) {
				if (key === "parentId" && props.rowInfo[key] === "0") {
					// 若 key 是 parentId 且值为 "0"，则不赋值
					continue;
				}
				form[key] = props.rowInfo[key];
			}
		}
	}
	onMounted(async () => {
		const res = await getMenuTree();
		treeData.value = res;
	});
	// registerFormDone(async () => {
	// 	await saveMenu(Object.assign({}, toRaw(form), props.rowInfo ? { id: props.rowInfo.id } : {}));
	// 	return true;
	// });
	registerFormDone(async () => {
		let submitData = { ...toRaw(form) };

		if (submitData.menuType === 1) {
			// 当菜单类型为 1（菜单）时，清除按钮标识
			submitData.buttonSign = "";
		} else if (submitData.menuType === 2) {
			// 当菜单类型为 2（按钮）时，清除与菜单相关的字段
			const menuFields = ["routerName", "routerUrl", "vueUrl", "redirectType", "icon"];
			menuFields.forEach((field) => {
				submitData[field] = "";
			});
		}

		// 合并行信息中的 id
		if (props.rowInfo) {
			submitData = { ...submitData, id: props.rowInfo.id };
		}

		await saveMenu(submitData);
		return true;
	});
</script>
