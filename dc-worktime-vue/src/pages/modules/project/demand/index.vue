<template>
	<BasePage :asideRightWidth="600">
		<template #pageRight>
			<div v-if="detailInfo.title" class="task-info-wrap">
				<div class="title">{{ detailInfo.title }}</div>
				<div class="flex">
					<div class="task-item m-r-16px m-b-16px">
						<div class="flex flex-items-center">
							<div class="icon-bg">{{ detailInfo.personChargeName?.substring(0,1) }}</div>
							<div>
								<div class="name">{{ detailInfo.personChargeName || '--' }}</div>
								<div class="type">负责人</div>
							</div>
						</div>
					</div>
					<div class="task-item m-b-16px">
						<div class="flex flex-items-center">
							
							<img v-if="detailInfo.state == 2" style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/status2.png" alt="">
							<img v-else-if="detailInfo.state == 3" style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/status3.png" alt="">
							<img v-else-if="detailInfo.state == 4" style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/status4.png" alt="">
							<img v-else style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/status1.png" alt="">
							<div>
								<div class="name">{{ detailInfo.state == 4 ? '已作废' : detailInfo.state == 3?'已完成' :  detailInfo.state == 2 ? '进行中' :'未开始'}}</div>
								<div class="type">当前状态</div>
							</div>
						</div>
						<ElDropdown @command="handleCommand">
							<span class="el-dropdown-link">
								<ElIcon><CaretBottom /></ElIcon>
							</span>
							<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item command="1">未开始</el-dropdown-item>
								<el-dropdown-item command="2">进行中</el-dropdown-item>
								<el-dropdown-item command="3">已完成</el-dropdown-item>
								<el-dropdown-item command="4">作废</el-dropdown-item>
							</el-dropdown-menu>
							</template>
						</ElDropdown>
					</div>
				</div>
				<div class="flex">
					<div class="task-item m-r-16px">
						<div class="flex flex-items-center">
							<img style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/class-icon.png" alt="">
							<div>
								<div class="name">{{ detailInfo.priorityLabel || '--' }}</div>
								<div class="type">优先级</div>
							</div>
						</div>
						<ElDropdown @command="handlePriority" v-if="detailInfo.state != 3">
							<span class="el-dropdown-link">
								<ElIcon><CaretBottom /></ElIcon>
							</span>
							<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item v-for="item in priorityList" :key="item.value" :command="item.value">{{ item.label }}</el-dropdown-item>
							</el-dropdown-menu>
							</template>
						</ElDropdown>
					</div>
					<div class="task-item">
						<div class="flex flex-items-center">
							<img style="width:36px;height: 36px;margin-right: 12px" src="@/assets/project/plan-icon.png" alt="">
							<div style="text-align: left">
								<div class="name">{{detailInfo.planName || '未设置'}}</div>
								<div class="type">所属计划</div>
							</div>
						</div>
						<div style="position: relative">
							<ElIcon @click="handleShow"><CaretBottom /></ElIcon>
							<ElCascaderPanel
								@change="changePlane"
								v-model="plan"
								v-show="isShow"
								:props="{value: 'id', label:'title'}"
								style="width: fit-content;position:fixed;right:60px;top:272px;background:#fff;z-index:20"
								:options="planTreeData">
								<template #default="{ node, data }">
									<ElTooltip
										class="box-item"
										effect="dark"
										:content="data.title"
										placement="top"
									>
										<span>{{ data.title }}</span>
									</ElTooltip>
								</template>
							</ElCascaderPanel>
						</div>
						
					</div>
				</div>
				<ElDivider class="m-t-24px m-b-24px"></ElDivider>
				<div class="title">描述</div>
				<BaseForm v-bind="form" />
				<div style="text-align:right">
					<ElButton @click="handleCancel">放弃修改</ElButton>
					<ElButton type="primary" @click="handleSave" class="m-r-12px">保存描述内容</ElButton>
				</div>
				<ElDivider></ElDivider>
				<ElDescriptions :column="1" class="custom-descri2" label-width="80px">
					<ElDescriptionsItem label="负责人：">
						{{ detailInfo.personChargeName }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="优先级：">
						{{ detailInfo.priorityLabel }}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="状态：">
						{{ detailInfo.state == 3?'已完成' :  detailInfo.state == 2 ? '进行中' :'未开始'}}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="所属计划：">
						{{detailInfo.planName || '未设置'}}
					</ElDescriptionsItem>
					<ElDescriptionsItem label="需求来源：">
						{{detailInfo.requireSource || '--'}}
					</ElDescriptionsItem>
					
				</ElDescriptions>
			</div>
			<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
				<img src="@/assets/no-Data.png" style="width: 200px;" />
				<span style="margin-top: 10px;color: #999">暂无数据</span>
			</div>
		</template>
		<template #pageBody>
			<div style="position: relative;height: 100%">
				<div class="left-Info">
					<span class="m-r-10px">共 {{ list.length }} 条</span>
					<ElButton :icon="CirclePlus" type="success" @click="handleAdd">
						提需求
					</ElButton>
				</div>
				<div style="text-align: left;padding: 6px 0">
					<span>项目迭代：</span>
					<ElSelect
						style="width: 150px"
						v-model:model-value="iteration.currentId"
						placeholder="请选择迭代"
						@change="handleChangeIteration"
					>
						<ElOption
							v-for="item in iteration.list"
							:key="item.id"
							:label="item.iteration_name"
							:value="item.id"
						>
						</ElOption>
					</ElSelect>
				</div>
				<ElTabs v-model="activeName" @click="handleTab" style="height:calc(100% - 44px)">
					<ElTabPane label="全部需求" name="0" class="m-b-16px heightAuto">
						<template #label>
							<span class="custom-tabs-label">
								全部需求
								<div class="tag">
									{{countAll.all - 99 > 0 ? "99+" : countAll.all}}
								</div>
							</span>
						</template>
						<div v-if="list.length > 0">
							<div class="flex task-card" v-for="(item,index) in list" :key="index">
									<ListItem :activeId="activeId" :item="item" @click.stop="handleItem(item.id, index)" />
							</div>
						</div>
						<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
							<img src="@/assets/no-Data.png" style="width: 200px;" />
							<span style="margin-top: 10px;color: #999">暂无数据</span>
						</div>
					</ElTabPane>
					<ElTabPane label="未开始" name="1" class="m-b-16px heightAuto">
						<template #label>
							<span class="custom-tabs-label">
								未开始
								<div class="tag">
									{{countAll.notStart - 99 > 0 ? "99+" : countAll.notStart}}
								</div>
							</span>
						</template>
						<div v-if="list.length > 0">
							<div class="flex task-card" v-for="(item,index) in list" :key="index">
								<ListItem :activeId="activeId" :item="item" @click.stop="handleItem(item.id, index)" />
							</div>
							<!-- <ElPagination background layout="prev, pager, next" :currentPage="pageNo" :pageSize="pageSize" :total="1000" /> -->
						</div>
						<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
							<img src="@/assets/no-Data.png" style="width: 200px;" />
							<span style="margin-top: 10px;color: #999">暂无数据</span>
						</div>
					</ElTabPane>
					<ElTabPane label="进行中" name="2" class="m-b-16px heightAuto">
						<template #label>
							<span class="custom-tabs-label">
								进行中
								<div class="tag">
									{{countAll.underWay - 99 > 0 ? "99+" : countAll.underWay}}
								</div>
							</span>
						</template>
						<div v-if="list.length > 0">
							<div class="flex task-card" v-for="(item,index) in list" :key="index">
								<ListItem :activeId="activeId" :item="item" @click.stop="handleItem(item.id, index)" />
							</div>
							<!-- <ElPagination background layout="prev, pager, next" :currentPage="pageNo" :pageSize="pageSize" :total="1000" /> -->
						</div>
						<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
							<img src="@/assets/no-Data.png" style="width: 200px;" />
							<span style="margin-top: 10px;color: #999">暂无数据</span>
						</div>
					</ElTabPane>
					<ElTabPane label="已完成" name="3" class="m-b-16px heightAuto">
						<template #label>
							<span class="custom-tabs-label">
								已完成
								<div class="tag">
									{{countAll.finished - 99 > 0 ? "99+" : countAll.finished}}
								</div>
							</span>
						</template>
						<div v-if="list.length > 0">
							<div class="flex task-card" v-for="(item,index) in list" :key="index">
								<ListItem :activeId="activeId" :item="item" @click.stop="handleItem(item.id, index)" />
							</div>
							<!-- <ElPagination background layout="prev, pager, next" :currentPage="pageNo" :pageSize="pageSize" :total="1000" /> -->
						</div>
						<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
							<img src="@/assets/no-Data.png" style="width: 200px;" />
							<span style="margin-top: 10px;color: #999">暂无数据</span>
						</div>
					</ElTabPane>
					<ElTabPane label="作废" name="4" class="m-b-16px heightAuto">
						<template #label>
							<span class="custom-tabs-label">
								作废
								<div class="tag">
									{{countAll.discard - 99 > 0 ? "99+" : countAll.discard}}
								</div>
							</span>
						</template>
						<div v-if="list.length > 0">
							<div class="flex task-card" v-for="(item,index) in list" :key="index">
								<ListItem :activeId="activeId" :item="item" @click.stop="handleItem(item.id, index)" />
							</div>
							<!-- <ElPagination background layout="prev, pager, next" :currentPage="pageNo" :pageSize="pageSize" :total="1000" /> -->
						</div>
						<div v-else class="flex flex-1 flex-items-center flex-col flex-justify-center" style="height:100%">
							<img src="@/assets/no-Data.png" style="width: 200px;" />
							<span style="margin-top: 10px;color: #999">暂无数据</span>
						</div>
					</ElTabPane>
				</ElTabs>
			</div>
		</template>
	</BasePage>
</template>

<script setup lang="tsx">
import { ref, onMounted, computed, reactive } from "vue";
import { createDrawerAsync } from "@/core/dialog";
import BasePage from "@/core/struct/page/base-page";
import BaseForm from "@/core/struct/form/base-form";
import { useDialogStructForm } from "@/core/struct/form/use-base-form";
import { appInstance } from "@/main";
import { markRaw } from "vue";
import { CirclePlus, CaretBottom, Right } from "@element-plus/icons-vue";
import { ElIcon, ElMessageBox, ElMessage, type Action } from "element-plus";
import { ElDivider } from "element-plus";
import ListItem from "./components/list-item.vue";
import { deleteById, selectPageList, getEntityById, saveOrUpd, countRequirement } from "@/api/project/demand";
import AddDemand from "./dialog/add-demand.vue";
import { selectPlanTree } from "@/api/project/plan";
import { getIterationList } from "@/api/project/plan";
import { iterationTree } from "@/api/common/login";

const activeName = ref("0");

const { form } = useDialogStructForm({
		expand: false,
		labelWidth: 60,
		showMessage: true,
		inlineMessage: false,
		labelPosition: "top",
		formItems: [
		{
				label: "",
				value: "",
				type: "Editor",
				prop: "description",
				span: 24,
				attrs: {
					style: { height: "200px", width: "100%" },
					contentType: "html"
				}
			},
		],
		rules: {
			description: [
				{
					required: true,
					message: "请输入",
					trigger: "blur"
				}
			],
		}
	});
// 计划渲染数据
const planTreeData = ref([])
// 计划回显需要用到的数据
// const planList = ref([])
// 迭代计划
const iteration = reactive({
	list: [],
	currentId:''
})
onMounted(async () => {
	iteration.list = await getIterationList({ project_id: appInstance.projectItem.id });
	iteration.currentId = iteration.list[0]?.id
	getPlanTree(iteration.currentId)
	getList('', 'tab')
});

// 切换迭代
const handleChangeIteration = (e: number) => {
	if (list.value) list.value = [];
	getPlanTree(e);
	getList('', 'tab')
};

// 根据迭代id获取计划树
const getPlanTree = async (iteration_id: number | string) => {
	planTreeData.value = await iterationTree({
		project_id: appInstance.projectItem.id,
		iteration_id
	});
	// planList.value = planTreeData.value.reduce((list, item) => {
	// 	list.push(item);
	// 	if (item.children && item.children.length > 0) {
	// 		for (let i = 0; i < item.children.length; i++) {
	// 			const element = item.children[i];
	// 			list.push(element);
	// 		}
	// 	}
	// 	return list;
	// }, []);
};

const priorityList = computed(() => {
	return appInstance.mapDict.priority
})
const pageNo = ref(1)
const pageSize = ref(20)
const countAll = ref({})
const getList = (state:any, type) => {
	selectPageList({pageNo: 1,pageSize: 1000,sortDate: 2, state: state,projectId: appInstance.projectItem.id,iteration_id: iteration.currentId}).then(res => {
		list.value = res.records
		if(type == 'tab') {
			activeId.value = res.records[0]?.id
		}
		const temp = res.records.filter(val => val.id == activeId.value)
		detailInfo.value = temp[0] || {}
		plan.value = detailInfo.value.planId
		form.value.description = detailInfo.value.description
	})
	countRequirement(appInstance.projectItem.id,iteration.currentId).then(res => {
		countAll.value = res
	})
}

const handleTab = () => {
	getList(activeName.value != '0' ? activeName.value: '', 'tab')
	pageNo.value = 1
}

const list = ref([])
const activeId = ref('')
const detailInfo = ref({})
const currentIndex = ref()
const handleItem = (id, index) => {
	activeId.value = id
	currentIndex.value = index
	getEntityById(id).then((res) => {
		detailInfo.value = res || {}
		plan.value = detailInfo.value.planId
		form.value.description = res.description
	})
}

// 放弃修改
const handleCancel = () => {
	form.value.description = detailInfo.value?.description
}
// 保存修改
const handleSave = () => {
	saveOrUpd({id: detailInfo.value.id, description: form.value.description}).then(res => {
		ElMessage.success("添加成功");
		getList(activeName.value != '0' ? activeName.value: '')
	})
}

// 审核
const handleCommand = (val) => {
	if(!detailInfo.value.id) return ElMessage.warning("请选择一个需求");
	saveOrUpd({id: detailInfo.value.id, state: val}).then(res => {
		getList(activeName.value != '0' ? activeName.value: '')
	})
}


// 计划面板是否出现
const isShow = ref(false)
const handleShow = () => {
	isShow.value = !isShow.value
}

// 改变计划
const plan = ref('')
const changePlane = (val) => {
	console.log('22222222222222222val', val)
	if(!detailInfo.value.id) return ElMessage.warning("请选择一个需求");
	const planId = val.slice(-1);
	const iteration_id = Math.abs(val[0])
	// const fList = planList.value.filter((item) => item.id == planId.slice(-1)[0]);
	saveOrUpd({
		id: detailInfo.value.id,
		planId: planId.length == 0 ? null : planId.length == 1 ? planId[0] : planId[1],
		// planType: fList.length > 0 ? fList[0].plan_type : null,
		iteration_id
	}).then(res => {
		isShow.value = false
		getList(activeName.value != '0' ? activeName.value: '')
	})
}

// 优先级
const handlePriority = (val) => {
	if(!detailInfo.value.id) return ElMessage.warning("请选择一个需求");
	saveOrUpd({id: detailInfo.value.id, priority: val}).then(res => {
		getList(activeName.value != '0' ? activeName.value: '')
	})
}
const handleAdd = () => {
	createDrawerAsync(
		{ title: "新增需求", showNext: false, width: "800px", closeOnClickModal: false,closeOnPressEscape: false },
		{},
		<AddDemand iterationId={iteration.currentId} />
	).then(() => {
		getList('')
	});
}
</script>
<style scoped lang="scss">
.task-item {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 70px;
	background: #ffffff;
	box-shadow: 0px 0px 12px 0px rgba(230, 237, 241, 0.5);
	border-radius: 8px 8px 8px 8px;
	border: 1px solid rgba(0, 0, 0, 0.06);
	padding: 14px 16px;
}

.source {
	border: 1px solid rgba(0,0,0,0.06);
	padding: 16px;
	border-radius: 8px;
}
.task-info-wrap {
	padding: 24px 32px;

	.title {
		font-size: 16px;
		color: rgba(0,0,0,0.85);
		line-height: 24px;
		text-align: left;
		margin-bottom: 16px;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
		
	}

	.icon-bg {
		width: 36px;
		height: 36px;
		line-height: 36px;
		background: #1B86FF;
		border-radius: 77px 77px 77px 77px;
		margin-right: 12px;
		color: #fff;
	}

	.name {
		font-size: 14px;
		color: rgba(0,0,0,0.85);
		line-height: 22px;
		height: 22px;
		text-overflow: ellipsis;
		overflow: hidden;
		white-space: nowrap;
		max-width: 160px;
		text-align: left;
		font-weight: bold;
	}

	.type {
		font-size: 12px;
		color: rgba(0,0,0,0.65);
		line-height: 20px;
	}
}

.custom-tabs-label {
	display: flex;
	align-items: center;
	padding:0 24px;
	.tag {
		padding:0 5px;
		height: 20px;
		display: flex;
		align-items: center;
		text-align: center;
		font-size: 14px;
		color: #fff;
		background: #F56C6C;
		border-radius: 27px 27px 27px 27px;
		border: 1px solid #FFFFFF;
		margin-left: 4px;
	}
}

.left-Info {
	position: absolute;
	right:0;
	top:6px;
	z-index:1;
}

.heightAuto {
	height: 100%;
	overflow: auto;
}

::v-deep {
	.el-descriptions__cell {
		display: flex;
	}
	.el-descriptions__content {
		flex:1;
	}

	.el-tabs__item {
		padding: 0;
	}

	.el-cascader-node__label {
		width: 206px;
	}

	.el-radio__inner {
		border: 1px solid rgba(0,0,0,0.15);
	}
}
</style>
