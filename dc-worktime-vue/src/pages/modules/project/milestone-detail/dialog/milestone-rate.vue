<template>
	<div>
		<ElForm :model="data.form" :rules="rules" class="notMargn" ref="formInstance" scroll-to-error>
			<ElCard shadow="never" class="m-t-10px">
				<div class="flex flex-items-center">
					<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
					<span class="m-l-10px">项目里程碑/阶段信息</span>
				</div>
				<div class="m-t-20px">
					<ElDescriptions :column="1">
						<ElDescriptionsItem label="迭代名称:">{{ data.detail?.iterationName }}</ElDescriptionsItem>
						<ElDescriptionsItem label="阶段名称:">
							<el-link v-if="data.detail?.groupType == 1" link>需求阶段</el-link>
							<el-link v-else-if="data.detail?.groupType == 2" link>设计阶段</el-link>
							<el-link v-else-if="data.detail?.groupType == 3" link>开发阶段</el-link>
							<el-link v-else-if="data.detail?.groupType == 4" link>内部评审阶段</el-link>
							<el-link v-else link>--</el-link>
						</ElDescriptionsItem>
						<ElDescriptionsItem label="里程碑阶段:">{{ data.detail?.milestoneName }}</ElDescriptionsItem>
					</ElDescriptions>
				</div>
			</ElCard>
			<ElCard shadow="never" class="m-t-10px">
				<div class="flex flex-items-center">
					<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
					<span class="m-l-10px">评审信息</span>
				</div>
				<div class="m-t-20px">
					<ElDescriptions :column="1">
						<ElDescriptionsItem label="评审时间：">{{ dayjs().format("YYYY-MM-DD") }}</ElDescriptionsItem>
						<ElDescriptionsItem label="评审地点:">
							<ElFormItem prop="reviewPlace" style="margin-top: 10px">
								<ElInput placeholder="请输入" v-model:model-value="data.form.reviewPlace" />
							</ElFormItem>
						</ElDescriptionsItem>

						<ElDescriptionsItem label="评审内容描述：">
							<ElFormItem prop="rateContent" style="margin-top: 10px">
								<ElInput
									placeholder="请输入"
									v-model:model-value="data.form.rateContent"
									type="textarea"
									:rows="4"
								/>
							</ElFormItem>
						</ElDescriptionsItem>
						<ElDescriptionsItem label="参会成员：">
							<ElFormItem prop="users" style="margin-top: 10px">
								<ElSelect multiple v-model:model-value="data.form.users">
									<ElOption
										v-for="item in data.userInfoList"
										:key="item.id"
										:label="item.realName"
										:value="item.id"
									></ElOption>
								</ElSelect>
							</ElFormItem>
						</ElDescriptionsItem>
					</ElDescriptions>
				</div>
			</ElCard>
			<ElCard shadow="never" class="m-t-10px">
				<div class="flex flex-items-center flex-justify-between">
					<div class="flex flex-items-center">
						<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
						<span class="m-l-10px">评审问题记录</span>
					</div>
					<ElButton plain @click="handleAddBug">记录问题</ElButton>
				</div>
				<div class="m-t-20px">
					<ElTable border :data="data.data">
						<ElTableColumn label="ID" prop="id" width="80px" align="center"
							><template #default="scope">
								{{ scope.$index + 1 }}
							</template></ElTableColumn
						>
						<ElTableColumn label="板块" align="center" v-if="showBug">
							<template #default> 质量问题 </template>
						</ElTableColumn>
						<ElTableColumn label="问题类型" prop="question_type_name" align="center" v-if="!showBug">
						</ElTableColumn>
						<!-- question_type_name -->
						<ElTableColumn label="类型" align="center" v-if="showBug">
							<template #default="scope">
								<span v-if="scope.row.severity == 1">轻微bug</span>
								<span v-else-if="scope.row.severity == 2">一般bug</span>
								<span v-else-if="scope.row.severity == 3">重大bug</span>
							</template>
						</ElTableColumn>
						<ElTableColumn label="问题标题" align="center">
							<template #default="scope">
								<div class="desc-img" v-html="scope.row.bug_title"></div>
							</template>
						</ElTableColumn>
						<ElTableColumn label="责任人" width="80px" align="center" v-if="showBug">
							<template #default="scope">
								<div>
									{{ scope.row.assigned_to_user_type[0] == 1 ? "前端" : "后端" }}
								</div>
							</template>
						</ElTableColumn>
						<ElTableColumn label="责任人" width="120px" prop="assigned_to_user_name" align="center" v-else>
						</ElTableColumn>
						<ElTableColumn label="操作" align="center" width="120px">
							<template #default="scope">
								<ElButton link type="primary" @click="handleBugListEdit(scope)">编辑</ElButton>
								<ElButton link type="primary" @click="handleBugListDelete(scope)">删除</ElButton>
							</template>
						</ElTableColumn>
					</ElTable>
				</div>
			</ElCard>
			<ElCard shadow="never" class="m-t-10px" v-if="groupType != 1 && groupType != 2">
				<div class="flex flex-items-center">
					<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
					<span class="m-l-10px">其他指标评价(满分10分)</span>
				</div>
				<div class="m-t-20px">
					<div class="flex flex-items-center flex-justify-between">
						<span class="font-size-14px"
							>良好的用户体验评分：界面设计友好，操作流畅，无明显的设计不合理之处</span
						>
						<ElRate
							show-score
							text-color="#1B86FF"
							:colors="['#1B86FF', '#1B86FF', '#1B86FF']"
							score-template="{value} 分"
							@change="handleRateChange"
							v-model="rateData.userExperiencePoint"
						/>
					</div>
					<div class="flex flex-items-center m-t-20px flex-justify-between">
						<span class="font-size-14px">逻辑清晰评分：代码结构合理，业务逻辑明确，易于理解和维护</span>
						<ElRate
							show-score
							text-color="#1B86FF"
							v-model="rateData.clearLogicPoint"
							@change="handleRateChange"
							:colors="['#1B86FF', '#1B86FF', '#1B86FF']"
							score-template="{value} 分"
						/>
					</div>
				</div>
			</ElCard>
			<ElCard shadow="never" class="m-t-10px">
				<div class="flex flex-items-center flex-justify-between">
					<div class="flex flex-items-center">
						<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
						<span class="m-l-10px">评审总结信息</span>
					</div>
					<div class="flex flex-items-center gap-10px">
						<span class="font-size-14px"> 按期完成评分标准： </span>
						<DelayRate :rate="40" />
						<DelayRate :rate="30" />
						<DelayRate :rate="20" />
						<DelayRate :rate="0" />
					</div>
				</div>
				<div class="m-t-20px">
					<!-- 原型阶段 -->
					<PrototypeRate v-if="groupType == 1" :data="rateData" />
					<!-- 设计阶段 -->
					<DesignRate v-else-if="groupType == 2" :data="rateData" />
					<!-- 内部评审阶段 和 开发阶段-->
					<BugRate v-else :data="rateData" />
				</div>
			</ElCard>
			<ElCard shadow="never" class="m-t-10px">
				<div class="flex flex-items-center m-b-20px">
					<img w="24px" h="24px" src="../../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
					<span class="m-l-10px">评审意见</span>
				</div>
				<ElInput type="textarea" v-model:model-value="data.form.reviewComments" :rows="4"></ElInput>
			</ElCard>
		</ElForm>
	</div>
</template>

<script lang="tsx" setup>
	import {
		ElInput,
		ElDescriptions,
		ElCard,
		ElDescriptionsItem,
		ElTable,
		ElTableColumn,
		ElButton,
		ElSelect,
		ElOption,
		ElForm,
		ElFormItem,
		ElRate,
		type FormRules
	} from "element-plus";
	import { reactive, computed, nextTick } from "vue";
	import AddBug from "./add-bug.vue";
	import AddProblem from "./add-problem.vue";
	import { createModelAsync } from "@/core/dialog";
	import { number } from "vue-types";
	import { getEntityById } from "@/api/project/milestone";
	import { appInstance } from "@/main";
	import dayjs from "dayjs";
	import { useDialogForm } from "@/core/dialog/dialog-container";
	import { saveReview } from "@/api/project/deliverables";
	import { projectUserList } from "@/api/project/list";
	import { useRate } from "../rate";
	import BugRate from "../components/bug-rate.vue";
	import DesignRate from "../components/design-rate.vue";
	import PrototypeRate from "../components/prototype-rate.vue";
	import DelayRate from "../components/delay-rate.vue";
	const props = defineProps({
		id: number().def(),
		groupType: number().def(1)
	});

	const { getFinalRate, rateData } = useRate(props.groupType);

	const showBug = computed(() => {
		return props.groupType != 1 && props.groupType != 2;
	});

	const data = reactive({
		data: [] as any[],
		detail: null as any,
		userInfoList: [] as any[],
		form: {
			users: [],
			rateContent: "",
			reviewComments: "",
			reviewPlace: ""
		}
	});

	const rules = reactive<FormRules>({
		users: [{ required: true, message: "请选择", trigger: "change" }],
		rateContent: [{ required: true, message: "请输入", trigger: "blur" }],
		reviewPlace: [{ required: true, message: "请输入", trigger: "blur" }]
	});

	const objectSpanMethod = ({ rowIndex, columnIndex }: any) => {
		if (showBug.value) {
			if (columnIndex === 1) {
				if (rowIndex % 5 == 0) {
					return {
						rowspan: data.data.length,
						colspan: 1
					};
				} else {
					return {
						rowspan: 0,
						colspan: 0
					};
				}
			}
		} else {
			return {
				rowspan: 1,
				colspan: 1
			};
		}
	};

	const handleBugListEdit = ({ $index }: any) => {
		if (!showBug.value) {
			createModelAsync(
				{
					title: "记录问题",
					width: "1000px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddProblem groupType={props.groupType} data={data.data[$index]} />
			).then((res: any) => {
				data.data[$index] = { ...res };
				getFinalRate(data.detail?.appointFinishTime, data.data);
			});
		} else {
			createModelAsync(
				{ title: "记录问题", width: "1000px", closeOnClickModal: false, closeOnPressEscape: false },
				{},
				<AddBug groupType={props.groupType} data={data.data[$index]} />
			).then((res: any) => {
				// 指派前端和后端的话，需要同时加两条
				data.data.splice($index, 1);
				nextTick(() => {
					if (res.assigned_to_user_type.length == 2) {
						data.data.push({ ...res, assigned_to_user_type: ["1"] });
						data.data.push({ ...res, assigned_to_user_type: ["2"] });
					} else {
						data.data.push({ ...res });
					}
					getFinalRate(data.detail?.appointFinishTime, data.data);
				});
			});
		}
	};

	const handleBugListDelete = ({ $index }: any) => {
		data.data.splice($index, 1);
		getFinalRate(data.detail?.appointFinishTime, data.data);
	};

	const handleAddBug = () => {
		if (props.groupType == 2 || props.groupType == 1) {
			createModelAsync(
				{
					title: "记录问题",
					width: "1000px",
					closeOnClickModal: false,
					closeOnPressEscape: false
				},
				{},
				<AddProblem groupType={props.groupType} />
			).then((res: any) => {
				data.data.push(res);
				getFinalRate(data.detail?.appointFinishTime, data.data);
			});
		} else {
			createModelAsync(
				{ title: "记录问题", width: "1000px", closeOnClickModal: false, closeOnPressEscape: false },
				{},
				<AddBug />
			).then((res: any) => {
				// 指派前端和后端的话，需要同时加两条
				if (res.assigned_to_user_type.length == 2) {
					data.data.push({ ...res, assigned_to_user_type: ["1"] });
					data.data.push({ ...res, assigned_to_user_type: ["2"] });
				} else {
					data.data.push(res);
				}
				getFinalRate(data.detail?.appointFinishTime, data.data);
			});
		}
	};

	const handleRateChange = () => {
		getFinalRate(data.detail?.appointFinishTime, data.data);
	};

	getEntityById({ id: props.id }).then((res) => {
		data.detail = res;

		if (res.proReview) {
			data.data = res.proReview.testBugs.map((item: any) => {
				const obj = {
					module_id: "",
					pro_feature_id: "",
					assigned_to_user_id: "",
					isChoke: "0",
					severity: "",
					assigned_to_user_type: "",
					end_time: "",
					bug_title: "",
					description: "<p><br></p>",
					question_type: "",
					question_type_name: "",
					assigned_to_user_name: "",
					id: ""
				};
				Object.keys(obj).forEach((key: any) => {
					// 需求阶段和设计阶段不需要这个字段
					if (key === "assigned_to_user_type" && !showBug.value) {
						return;
					}
					Reflect.set(obj, key, item[key]);
				});
				if (!showBug.value) {
					return {
						...obj
					};
				} else {
					return {
						...obj,
						// 需求阶段和设计阶段不需要这个字段
						assigned_to_user_type: [item.assigned_to_user_type]
					};
				}
			});
			data.form.rateContent = res.proReview.reviewDesc;
			data.form.reviewComments = res.proReview.reviewComments;
			data.form.reviewPlace = res.proReview.reviewPlace;
			data.form.users = res.proReview.participatingMember.split(",").map((item: string) => Number(item));
			rateData.userExperiencePoint = Number(res.proReview.userExperiencePoint);
			rateData.clearLogicPoint = Number(res.proReview.clearLogicPoint);
		}
		// computedRate();
		getFinalRate(data.detail?.appointFinishTime, data.data);
	});

	projectUserList({
		projectId: appInstance.projectItem.id
	}).then((res) => {
		data.userInfoList = res;
	});

	const { formInstance, registerFormDone } = useDialogForm();

	registerFormDone(async (type: number) => {
		await saveReview(
			Object.assign(
				{},
				{
					...rateData,
					reviewComments: data.form.reviewComments,
					reviewDesc: data.form.rateContent,
					projectId: appInstance.projectItem.id,
					milestoneId: props.id,
					participatingMember: data.form.users.join(","),
					postponeStatus: rateData.milestonePostponeDay == 0 ? 2 : rateData.milestonePostponeDay > 0 ? 3 : 1,
					reviewResult: rateData.reviewTotalPoints >= 50 ? 1 : 2,
					reviewPlace: data.form.reviewPlace,
					isTemporarily: type == 2 ? 1 : 0, // 0:正式提交 1:暂存
					testBugs: !showBug.value
						? data.data
						: data.data.map((item) => {
								let module_id = 0;
								if (typeof item.module_id == "number") {
									module_id = item.module_id;
								} else {
									module_id = item.module_id[item.module_id.length - 1];
								}
								return {
									...item,
									module_id,
									assigned_to_user_type: item.assigned_to_user_type[0]
								};
							})
				},
				data.detail?.proReview?.id ? { id: data.detail?.proReview?.id } : {}
			)
		);
		return true;
	});

	// onMounted(() => {
	// 	computedRate();
	// });
</script>
