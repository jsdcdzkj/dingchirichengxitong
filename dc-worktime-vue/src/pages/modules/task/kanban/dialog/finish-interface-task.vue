<template>
	<div>
		<div class="flex flex-items-center m-b-14px">
			<ElIcon class="m-r-8px" size="20px"
				><Warning color="#F56C6C"
			/></ElIcon>
			后端组人员任务结束时需提交接口信息以完成自动化测试，只有自动化测试通过后才可提交完成任务！
		</div>
		<ElDivider class="m-t-24px m-b-24px" />
		<div class="flex">
			<div class="left">
				<div class="add m-b-10px" @click="handleAdd">
					<ElIcon><Plus size="14px" /></ElIcon>
					新增
				</div>
				<div class="item" :class="{active: activeIndex == index}" v-for="(item,index) in interfaceList" :key="index" @click="handleClick(index, item)">
					<ElIcon v-if="item.result == '200'" size="16px"><CircleCheckFilled color="#428DF7"  /></ElIcon>
					<ElIcon v-if="item.result == '500'" size="16px"><CircleCloseFilled color="#F56C6C" /></ElIcon>
					<span class="m-l-12px">{{item.name}}</span>
				</div>
			</div>
			<div class="right flex-1">
				<BaseForm v-bind="form"></BaseForm>
				<div style="margin: 20px 0;text-align: center;" >
					<ElButton type="success" @click="handleCheck">接口验证</ElButton>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import BaseForm from "@/core/struct/form/base-form";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import {
	ElDivider,
	ElButton,
	ElMessage,
	ElIcon,
type FormInstance
} from "element-plus";
import { Warning, Plus, CircleCloseFilled, CircleCheckFilled } from "@element-plus/icons-vue";
import { ref } from "vue";
import {
	taskInterfaceSave,
	interfaceCheck,
	getFeatureList
} from "@/api/task/kanban";

const props = defineProps({
	types: {
		type: String,
		default: {}
	},
	row: {
		type: Object,
		default: {}
	}
});

const interfaceList = ref([{name: '接口名称', task_id: props.row.id, result:''}])
const formInstance = ref<FormInstance>()
const { form,registerFormDone } = useDialogStructForm({
	expand: false,
	labelWidth: 140,
	showMessage: true,
	inlineMessage: false,
	labelPosition: "right",
	formItems: [
		{
			label: "接口关联功能点",
			value: "",
			type: "ElSelect",
			prop: "feature_id",
			span: 24,
			attrs: {
				filterable: true,
			},
			select: {
				type: "ElOption",
				label: "featureName",
				value: "feature_id",
				list: [],
				listApi: async () => {
					const data = await getFeatureList({task_id: props.row.id, type: props.types});
					return data;
				},
			}
		},
		{
			label: "接口名称",
			value: "",
			type: "ElInput",
			prop: "name",
			span: 24,
		},
		{
			label: "接口类型",
			value: "",
			type: "ElSelect",
			prop: "method_type",
			span: 24,
			select: {
				type: "ElOption",
				label: "label",
				value: "value",
				list: [{
					label: 'post',
					value: 'post'
				},
				{
					label: 'get',
					value: 'get'
				}]
			}
		},
		{
			label: "接口路径",
			value: "",
			type: "ElInput",
			prop: "url",
			span: 24,
		},
		{
			label: "Content-Type",
			value: 1,
			type: "ElSelect",
			prop: "header_content_type",
			span: 24,
			select: {
				type: "ElOption",
				label: "label",
				value: "value",
				list: [{
					label: 'application/json',
					value: 1
				},
				{
					label: 'x-www-form-urlencoded',
					value: 2
				}]
			}
		},
		{
			label: "Params",
			value: "",
			type: "ElInput",
			prop: "paramsMap",
			span: 24,
			attrs: {
				type: 'textarea',
				rows: 4,
			},
		},
		{
			label: "token名称",
			value: "",
			type: "ElInput",
			prop: "token_name",
			span: 24,
		},
		{
			label: "token值",
			value: "",
			type: "ElInput",
			prop: "token_value",
			span: 24,
		},
		{
			label: "Response",
			value: "",
			type: "ElInput",
			prop: "response",
			span: 24,
			attrs: {
				type: 'textarea',
				disabled: true,
				rows: 6,
			},
		},
	],
	rules: {
		feature_id: [
			{
				required: true,
				message: "请输入",
				trigger: "change"
			}
		],
		name: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		method_type: [
			{
				required: true,
				message: "请选择",
				trigger: "change"
			}
		],
		url: [
			{
				required: true,
				message: "请输入",
				trigger: "blur"
			}
		],
		header_content_type: [
			{
				required: true,
				message: "请选择",
				trigger: "change"
			}
		],
	},
	onInstance(instance) {
		formInstance.value = instance
	},
},false);
const activeIndex = ref(0)
// 新增
const handleAdd = () => {
	interfaceList.value.unshift({name: '接口名称', task_id: props.row.id, result:''})
	activeIndex.value = 0
}

// 点击
const handleClick = (index:any, item:any) => {
	activeIndex.value = index
	form.value = item
}

// 判断一个字符串能不能转成对象
const canStringBeParsedAsObject = (str) => {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}
const handleCheck =() => {
	formInstance.value?.validate((valid) => {
		if (valid) {
			let paramsMap = {}
			if(form.value.paramsMap) {
				paramsMap = form.value.paramsMap.replace(/[\n\t\s]+/g, '')
				console.log('444444444444444paramsMap', paramsMap)
				if (canStringBeParsedAsObject(paramsMap)) {
					paramsMap = JSON.parse(paramsMap)
				} else {
					return ElMessage.warning('请填写正确的参数格式!')
				}
			}

			//接口验证
			interfaceCheck({...form.value, paramsMap}).then(res=> {
				console.log('33333333333333res', res)
				form.value.response = JSON.stringify(res)
				interfaceList.value[activeIndex.value] = {...form.value, task_id: props.row.id, result: '200'}
			}).catch((err) => {
				console.log('33333333333333res', err)
				form.value.response = err
				interfaceList.value[activeIndex.value] = {...form.value, task_id: props.row.id, result: '500'}
			})
			
		} else {
			return false
		}
	})
}
registerFormDone(async () => {
	let res = interfaceList.value.map(val => {
		if(val) 
		val.params = val.paramsMap
		delete val.paramsMap
		delete val.response
		return val
	})
	res = res.filter(val => val.result)
	if(res.length == 0) {
		ElMessage.warning("请至少填写一个验证接口");
		throw "请至少填写一个验证接口"
		return
	}
	await taskInterfaceSave({taskInterfaceList: res, id: props.row.id})
	return true;
});

</script>
<style lang="scss" scoped>
.left {
	border-right: 1px solid rgba(0,0,0,0.06);
	margin-right: 24px;
	padding: 0 24px;

	.add {
		width: 182px;
		height: 44px;
		text-align: center;
		line-height: 44px;
		background: rgba(255,255,255,0);
		border-radius: 8px 8px 8px 8px;
		border: 1px dashed rgba(0,0,0,0.15);
		font-size: 14px;
		color: rgba(0,0,0,0.65);
	}
	.item {
		width: 182px;
		height: 44px;
		display: flex;
		align-items: center;
		background: rgba(255,255,255,0);
		border-radius: 8px 8px 8px 8px;
		padding-left:12px;
	}
	.active {
		background: #E8F3FF;
		border-radius: 8px 8px 8px 8px;
	}
}

</style>