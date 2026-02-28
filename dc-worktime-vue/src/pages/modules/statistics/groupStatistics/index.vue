<template>
	<BasePage :hasGap="false">
		<template #pageSearch>
			<div class="tabs_box flex">
				<div class="tabs flex-1">
					<div
						:class="{ active: groupInfo.id == el.id }"
						v-for="el in groupList"
						:key="el.id"
						@click="setGroupInfo(el)"
					>
						{{ el.name }}
					</div>
				</div>
				<el-radio-group v-model="tabPage">
					<el-radio-button label="工作安排与跟踪日历" :value="1" />
					<el-radio-button label="工作内容分析" :value="2" />
				</el-radio-group>
			</div>
		</template>
		<template #pageBody>
			<calendar :groupInfo="groupInfo" v-show="tabPage == 1"></calendar>
			<workContent :groupInfo="groupInfo" v-if="tabPage == 2"></workContent>
		</template>
	</BasePage>
</template>
<script setup>
	import BasePage from "@/core/struct/page/base-page";
	import calendar from "./components/calendar.vue";
	import workContent from "./components/workContent.vue";
	import { ref, onMounted } from "vue";
	import { getList, getGroupUserList } from "@/api/task/kanban";
	const groupList = ref([]);
	const groupUsers = ref([]);
	const tabPage = ref(1);
	const groupInfo = ref({});
	const groupUserId = ref("");
	const setGroupInfo = (item) => {
		console.log("item", item);
		groupInfo.value = item;
	};
	onMounted(() => {
		getList({ pageNo: 1, pageSize: 99 }).then((res) => {
			groupList.value = res;
			if (res.length > 0) {
				// 临时修改
				groupInfo.value = res[0];
			}
		});
	});
</script>
<style lang="scss" scoped>
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
