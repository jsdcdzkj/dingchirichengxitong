<template>
	<BasePage :hasGap="false">
		<template #pageSearch>
			<div class="tabs_box flex">
				<div class="tabs flex-1">
					<div
						:class="{ active: activeIndex == el.value }"
						v-for="(el, index) in groupList"
						:key="index"
						@click="changeGroup(el.value)"
					>
						{{ el.label }}
					</div>
				</div>
			</div>
		</template>
		<template #pageBody>
			<div class="search-title">
				<span class="name">项目助理绩效数据-{{time.replace('-0', '-Q')}}</span>
				<!-- <ElDatePicker
					v-model="time"
					class="time-type"
					type="quarter"
					key="quarter"
					format="YYYY [Q]Q"
					value-format="YYYY-Q"
					placeholder="选择季度"
					@change="handleQuarterChange"
				/> -->
				
				<el-quarter-picker v-model="time" @change="handleQuarterChange" placeholder="选择季度" />
			</div>
			<ElTable :data="tablelist" border class="m-t-24px" v-loading="loading" stripe height="calc(100vh - 240px)">
				<ElTableColumn label="序号" width="80" align="center" type="index"> </ElTableColumn>
				<ElTableColumn
					prop="name"
					label="姓名"
					align="center"
					show-overflow-tooltip
				></ElTableColumn>
				<ElTableColumn
					v-if="[1, 4].includes(activeIndex)"
					label="项目验收一次性通过率计算公式"
					align="center"
					min-width="200px"
					show-overflow-tooltip
				>
					<template #default="scope">
						<span>内部评审总得分/内部评审总分值</span>
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="[1, 4].includes(activeIndex)"
					prop="rate1"
					label="项目验收一次性通过率"
					align="center"
					width="180px"
					show-overflow-tooltip
				>
					<template #default="scope">
						{{ scope.row.rate1 || '无数据' }}
					</template>
				</ElTableColumn>
				<ElTableColumn
					v-if="activeIndex == 1"
					label="测试BUG数量比重计算公式"
					align="center"
					min-width="210px"
					show-overflow-tooltip
				>
					<template #default="scope">
						<span>按季度统计，个人BUG总数/个人任务功能点总数</span>
					</template>
				</ElTableColumn>

				<ElTableColumn label="测试BUG数量比重" width="160px" v-if="activeIndex == 1" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate2 || '无数据' }}
					</template></ElTableColumn
				>
				<ElTableColumn label="工作计划按时完成率计算公式" min-width="210px" v-if="activeIndex == 2" align="center" show-overflow-tooltip
					><template #default="scope">
						按计划执行并通过内部评审项目数/参与项目总数
					</template>
				</ElTableColumn>
				<ElTableColumn label="工作计划按时完成率" width="200" v-if="activeIndex == 2" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate1 || '无数据' }}
					</template>
				</ElTableColumn>
				<ElTableColumn label="设计效果内部评审一次性通过率计算公式" min-width="210px" v-if="activeIndex == 2" align="center" show-overflow-tooltip
					><template #default="scope">
						内部评审总得分/内部评审总分值
					</template>
				</ElTableColumn>
				<ElTableColumn label="设计效果内部评审一次性通过率" width="240" v-if="activeIndex == 2" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate2 || '无数据' }}
					</template>
				</ElTableColumn>
				<!-- C++开发 -->
				<ElTableColumn label="工作计划按时完成率计算公式" v-if="activeIndex == 3" min-width="200" align="center" show-overflow-tooltip
					><template #default="scope">
						按计划执行并通过内部评审项目数/项目总数（概要设计、详细设计、编码、集成、测试、内部验收）
					</template>
				</ElTableColumn>
				<ElTableColumn label="工作计划按时完成率" v-if="activeIndex == 3" width="200" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate1 || '无数据' }}
					</template>
				</ElTableColumn>
				<ElTableColumn label="按时完成实施运维任务率计算公式" v-if="activeIndex == 3" min-width="200" align="center" show-overflow-tooltip
					><template #default="scope">
						按时完成运维实施任务次数/总运维实施任务数
					</template>
				</ElTableColumn>
				<ElTableColumn label="按时完成实施运维任务率" v-if="activeIndex == 3" width="200" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate2 || '无数据' }}
					</template>
				</ElTableColumn>
				<!-- 测试任务 -->
				<ElTableColumn label="测试报告bug覆盖率计算公式" min-width="200" v-if="activeIndex == 4" align="center" show-overflow-tooltip
					><template #default="scope">
						按季度统计，个人BUG总数/个人任务功能点总数
					</template>
				</ElTableColumn>
				<ElTableColumn label="测试报告bug覆盖率" width="180" v-if="activeIndex == 4" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate2 || '无数据' }}
					</template>
				</ElTableColumn>
				<ElTableColumn label="测试任务按期完成率计算公式" min-width="200" v-if="activeIndex == 4" align="center" show-overflow-tooltip
					><template #default="scope">
						按期完成任务数/任务总数   
					</template>
				</ElTableColumn>
				<ElTableColumn label="测试任务按期完成率" width="180" v-if="activeIndex == 4" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate3 || '无数据' }}
					</template>
				</ElTableColumn>
				<!-- 运维任务 -->
				<ElTableColumn label="按时完成实施运维任务率计算公式" min-width="200" v-if="[6, 5].includes(activeIndex)" align="center" show-overflow-tooltip
					><template #default="scope">
						按时完成运维实施任务次数/总运维实施任务数
					</template>
				</ElTableColumn>
				<ElTableColumn label="按时完成实施运维任务率" min-width="200" v-if="[6, 5].includes(activeIndex)" align="center" show-overflow-tooltip
					><template #default="scope">
						{{ scope.row.rate1 || '无数据' }}
					</template>
				</ElTableColumn>
			</ElTable>
		</template>
	</BasePage>
</template>
<script setup>
import BasePage from "@/core/struct/page/base-page";
import { ref, onMounted } from "vue";
import { reportKPI } from "@/api/performance/appraisal";
import { appInstance } from "@/main";
import ElQuarterPicker from "@/components/el-quarter-picker/index.vue"

const groupList = appInstance.mapDict.examine_team;
const activeIndex = ref(groupList[0].value);
console.log('group11111111', appInstance.mapDict)
const tablelist = ref([])
// 日期格式化函数
const formatDate = (date) => {
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, "0");
	const day = String(date.getDate()).padStart(2, "0");
	return `${year}-${month}-${day}`;
};

const getQuarter = () => {
    var now = new Date();
    var month = now.getMonth() + 1;
    var quarter;
    
    if (month >= 1 && month <= 3) {
        quarter = 1;
    } else if (month >= 4 && month <= 6) {
        quarter = 2;
    } else if (month >= 7 && month <= 9) {
        quarter = 3;
    } else {
        quarter = 4;
    }
    return quarter;
}
const currentYear = new Date().getFullYear()
const currentQuarter = getQuarter()
const time = ref(`${currentYear}-0${currentQuarter}`)
let startDate = ''
let endDate = ''
const getList = (id) => {
	reportKPI({
	startTime:startDate,
	endTime:endDate,
	team_id:id,
	}).then(res => {
		tablelist.value = res
	})
}
getList(groupList[0].value)
// 季度选择
// 处理按季选择
const handleQuarterChange = (quarter) => {
	if (quarter) {
		console.log('22222222222quarter',quarter)
		const [year, quarterNumber] = quarter.split("-").map(Number);
		// 计算本季度第一天
		const startMonth = (quarterNumber - 1) * 3;
		const startOfQuarter = new Date(year, startMonth, 1);
		// 计算本季度最后一天
		const endMonth = startMonth + 2;
		const endOfQuarter = new Date(year, endMonth + 1, 0);

		startDate = formatDate(startOfQuarter);
		endDate = formatDate(endOfQuarter);
		getList(activeIndex.value)
	}
};

const changeGroup = (id) => {
	activeIndex.value = id
	getList(id)
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
