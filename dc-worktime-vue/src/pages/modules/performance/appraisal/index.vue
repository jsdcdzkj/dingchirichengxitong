<template>
	<BasePage :hasGap="false">
		<template #pageSearch>
			<div class="tabs_box flex">
				<div class="tabs flex-1">
					<div v-show="el.dictValue!=3"
						:class="{ active: activeIndex == index }"
						v-for="(el,index) in groupList"
						:key="index"
						@click="changeGroup(el.dictValue,index,el.dictLabel)"
					>
					{{ el.dictLabel }}
					</div>
				</div>
			</div>
		</template>
		<template #pageBody>
			<div class="search-title">
				<span class="name">{{nameIndex}}绩效数据-{{ currentQuarter }}</span>
		
				<ElQuarter v-model="quarter" @change="handleQuarterChange" placeholder="选择季度" />

			</div>
			<ElTable :data="tablelist" border class="m-t-24px" v-loading="loading" stripe height="calc(100vh - 100px)">
				<ElTableColumn label="序号" width="80" align="center" type="index"> </ElTableColumn>
				<ElTableColumn
					prop="name"
					label="姓名"
					align="center"
					min-width="50px"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="[0,1,5].includes(activeIndex)" 
					label="项目验收一次性通过率计算公式"
					align="center"
					min-width="100px"
					show-overflow-tooltip
				>
					<template #default="scope">
					内部评审总得分/内部评审总分值
					</template>
				</ElTableColumn>
				<ElTableColumn
				v-if="[0,1,5].includes(activeIndex)" 
					prop="rate2"
					label="项目验收一次性通过率"
					align="center"
					min-width="80px"
					sortable 
					:sort-method="sortByRate2"
					show-overflow-tooltip
				>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate2_formula || '无数据'"
							placement="top-start"
						>
							{{ scope.row.rate2 ? scope.row.rate2+'%':"无数据" }}
						</ElTooltip>	
					</template>
			</ElTableColumn>
				<ElTableColumn	
					label="测试BUG数量比重计算公式"
					align="center"
					min-width="100px"
					show-overflow-tooltip
					v-if="[0,1].includes(activeIndex)" 
				>
			    <template #default="scope">
				按季度统计，个人BUG总数/个人任务功能点总数
				</template>
				</ElTableColumn>
				<ElTableColumn 	prop="rate1"  label="测试BUG数量比重" align="center"  min-width="80px" :sort-method="sortByRate1"  sortable		v-if="[0,1].includes(activeIndex)" show-overflow-tooltip>
               					
				<template #default="scope">
					<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate1_formula || '无数据'"
							placement="top-start"
						>
						{{ scope.row.rate1 ? scope.row.rate1+'%':"无数据" }}
					</ElTooltip>
				</template>
					
			   </ElTableColumn>
				<ElTableColumn   min-width="100px" label="工作计划按时完成率计算公式" v-if="[3,6].includes(activeIndex)"  align="center" show-overflow-tooltip
					><template #default="scope">
						  按计划执行并通过内部评审项目数/参与项目总数
					</template>
				</ElTableColumn>
				<ElTableColumn prop="rate1" min-width="80px" :sort-method="sortByRate1" sortable  label="工作计划按时完成率" v-if="[3,6].includes(activeIndex)"  align="center" show-overflow-tooltip
					>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate1_formula || '无数据'"
							placement="top-start"
						>
							{{ scope.row.rate1 ? scope.row.rate1+'%':"无数据" }}
						</ElTooltip>
					</template>
				</ElTableColumn>
				<ElTableColumn min-width="100px"  label="设计效果内部评审一次性通过率计算公式" v-if="activeIndex == 3" align="center" show-overflow-tooltip
					><template #default="scope">
				内部评审总得分/内部评审总分值
					</template>
				</ElTableColumn>
				<ElTableColumn prop="rate2" min-width="80px" :sort-method="sortByRate2" sortable  label="设计效果内部评审一次性通过率"  v-if="[3].includes(activeIndex)" align="center" show-overflow-tooltip
					>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate2_formula || '无数据'"
							placement="top-start"
						>
							{{ scope.row.rate2 ? scope.row.rate2+'%':"无数据" }}
						</ElTooltip>
					
					</template>
				</ElTableColumn>
				<!-- 测试任务 -->
				<ElTableColumn label="测试报告bug覆盖率计算公式" min-width="100px"  v-if="activeIndex == 5" align="center" show-overflow-tooltip
					><template #default="scope">
					<!-- 按季度统计，个人BUG总数/个人任务功能点总数 -->
					测试报告发现的bug数量/（内部评审时发现的bug数量+测试报告发现的bug数量）
					</template>
				</ElTableColumn>
				<ElTableColumn   prop="rate3"  label="测试报告bug覆盖率" :sort-method="sortByRate3" sortable  min-width="80px" v-if="activeIndex == 5" align="center" show-overflow-tooltip>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate3_formula || '无数据'"
							placement="top-start"
						>
						{{ scope.row.rate3 ? scope.row.rate3+'%':"无数据" }}
					</ElTooltip>	
					</template>
				</ElTableColumn>
				<ElTableColumn label="测试任务按期完成率计算公式" min-width="100px"  v-if="activeIndex == 5" align="center" show-overflow-tooltip
					><template #default="scope">
						按期完成任务数/任务总数
					</template>
				</ElTableColumn>
				<ElTableColumn  prop="rate1" label="测试任务按期完成率" :sort-method="sortByRate1"  sortable  min-width="80px"  v-if="activeIndex == 5" align="center" show-overflow-tooltip
					>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate1_formula || '无数据'"
							placement="top-start"
						>
						{{ scope.row.rate1 ? scope.row.rate1+'%':"无数据" }}
					</ElTooltip>
								
					</template>
				</ElTableColumn>
				<ElTableColumn label="按时完成实施运维任务率计算公式"  min-width="100px"  v-if="[4,6,7].includes(activeIndex)" align="center" show-overflow-tooltip
					><template #default="scope">
						按时完成运维实施任务次数/总运维实施任务数
					</template>
				</ElTableColumn>
				<ElTableColumn  prop="rate1" label="按时完成实施运维任务率" :sort-method="sortByRate1" sortable  min-width="80px"  v-if="[4,7].includes(activeIndex)" align="center" show-overflow-tooltip>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate1_formula || '无数据'"
							placement="top-start"
						>
						{{ scope.row.rate1 ? scope.row.rate1+'%':"无数据" }}
					</ElTooltip>
						
					</template>
				</ElTableColumn>

					<ElTableColumn  prop="rate2" label="按时完成实施运维任务率" :sort-method="sortByRate2" sortable  min-width="80px"  v-if="[6].includes(activeIndex)" align="center" show-overflow-tooltip>
					<template #default="scope">
						<ElTooltip
							class="box-item"
							effect="dark"
							:content="scope.row.rate2_formula || '无数据'"
							placement="top-start"
						>
						{{ scope.row.rate1? scope.row.rate2+'%':"无数据" }}
					</ElTooltip>
					</template>
				</ElTableColumn>
			</ElTable>
		</template>
	</BasePage>
</template>
<script setup>
import BasePage from "@/core/struct/page/base-page";
import { ref, onMounted, reactive, computed } from "vue";
import { reportKPI,getListSys } from "@/api/task/kanban";
import ElQuarter from "@/components/quarter/index.vue"
import { appInstance } from "@/main";
import axios from "axios"
import { selectDictList, deleteDict } from "@/api/setting/dictionary";
const loading = ref(true);
const tablelist=ref([])
let quarter=ref("")
let groupList = reactive([]);
const activeIndex = ref(0);
let nameIndex=ref("")
let teamId=ref("")
let queryParamsStartDate=ref("")
let queryParamsSendDate=ref("")
let groupInfo = ref({});
let currentQuarter=ref("")
let deep = []
		// 日期格式化函数
const formatDate = (date) => {
const year = date.getFullYear();
const month = String(date.getMonth() + 1).padStart(2, "0");
const day = String(date.getDate()).padStart(2, "0");
       return `${year}-${month}-${day}`;
};
const disabledDate = (time) => {
  return time.getTime() > Date.now() - 8.64e7// - 8.64e7是今天可以选
}
// groupList = appInstance.mapDict.examine_team

// 处理按季选择
const handleQuarterChange=(quarter)=>{
	if (quarter) {
		if(deep.length > 0){
			const source = deep[deep.length - 1]
			console.log(source);
			source.cancel("中断")
		}
		const  source = axios.CancelToken.source();
		deep.push(source)
		const [year, quarterNumber] = quarter.split("-0").map(Number);
		currentQuarter=year+"-Q"+quarterNumber
		// 计算本季度第一天
		const startMonth = (quarterNumber - 1) * 3;
		const startOfQuarter = new Date(year, startMonth, 1);
		// 计算本季度最后一天
		const endMonth = startMonth + 2;
		const endOfQuarter = new Date(year, endMonth + 1, 0);
		queryParamsStartDate = formatDate(startOfQuarter)
		queryParamsSendDate = formatDate(endOfQuarter)
		loading.value = true
		reportKPI({
			team_id:teamId.value,
			startTime:queryParamsStartDate,
			endTime:queryParamsSendDate
		},source.token).then((res) => {
			tablelist.value = res
	      	loading.value = false
			deep.splice(deep.length - 1,1)
		});

	}

}
// 自定义排序方法
const sortByRate1 = (a, b) => {
	const rateA = parseFloat(a.rate1) || 0;
	const rateB = parseFloat(b.rate1) || 0;
	return rateA - rateB;
};
// 自定义排序方法
const sortByRate2 = (a, b) => {
	const rateA = parseFloat(a.rate2) || 0;
	const rateB = parseFloat(b.rate2) || 0;
	return rateA - rateB;
};
// 自定义排序方法
const sortByRate3 = (a, b) => {
	const rateA = parseFloat(a.rate3) || 0;
	const rateB = parseFloat(b.rate3) || 0;
	return rateA - rateB;
};

onMounted(() => {
	//取组的新接口
	selectDictList({ dictSign: "sysGroup" }).then((data) => {
		groupList=data
		nameIndex.value=groupList[0].dictLabel
		teamId.value=groupList[0].dictValue
		const date=new Date()
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, "0");
		const query1=year+"-"+month
		console.log('++++++++++++++',query1)
		currentQuarter = computed(() => {
			return `${year}-Q${Math.floor((month - 1) / 3) + 1}`
		});
		quarter=`${year}-0${Math.floor((month - 1) / 3) + 1}`
		if(queryParamsStartDate.value==""){
				const [year1, quarterNumber1] = quarter.split("-").map(Number);
				// 计算本季度第一天
				const startMonth1 = (quarterNumber1 - 1) * 3;
				const startOfQuarter1 = new Date(year1, startMonth1, 1);
				// 计算本季度最后一天
				const endMonth1 = startMonth1 + 2;
				const endOfQuarter1 = new Date(year1, endMonth1 + 1, 0);
				queryParamsStartDate = formatDate(startOfQuarter1)
				queryParamsSendDate = formatDate(endOfQuarter1)
		}
		// loading.value = true
		// reportKPI({
		// 	team_id:groupList[0].dictValue,
		// 	startTime:queryParamsStartDate,
		// 	endTime:queryParamsSendDate
		// }).then((res) => {		
		// 	tablelist.value = res
		// 	loading.value = false
								
		// });
		changeGroup(groupList[0].dictValue, 0, groupList[0].dictLabel)

	});

});

const changeGroup = (id,index,name) => {
		loading.value = true
		console.log('返回的值', index)
		teamId.value=id
		activeIndex.value = index
		nameIndex.value=name
		console.log('deep.length', deep)
		if(deep.length > 0){
			const source = deep[deep.length - 1]
			console.log('33333333333333', deep, source)
			source.cancel("中断")
		}
		const  source = axios.CancelToken.source();
		deep.push(source)
			reportKPI({
				team_id:id,
				startTime:queryParamsStartDate,
				endTime:queryParamsSendDate
			},source.token).then((res) => {
				tablelist.value = res
				loading.value = false

				deep.splice(deep.length - 1,1)
			})
}

</script>
<style lang="scss" scoped>
.search-title {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-top:20px;
	.name {
		font-size: 18px;
		font-weight: 700;
	}
}
.tabs_box {
	justify-content: space-between;
	align-items: center;
	.tabs {
		overflow-x: auto;
		overflow-y: hidden;
		margin-right: 20px;
		display: flex;
		flex-wrap: nowrap;
		div {
			height: 48px;
			background: #ffffff;
			border-radius: 8px 8px 0px 0px;
			border: 1px solid rgba(0, 0, 0, 0.06);
			padding: 0 16px;
			margin-right: 8px;
			line-height: 48px;
			cursor: pointer;
			flex-shrink: 0;
			color: rgba(0, 0, 0, 0.65);
			font-size: 16px;
			&.active {
				background: linear-gradient(180deg, #ffffff 0%, #d0e6ff 100%);
				box-shadow: inset 0px 0px 4px 0px #1a81f4;
				border: none;
				border-bottom: 2px solid #1b86ff;
				color: #1b83f8;
			}
		}
	}
}
</style>
