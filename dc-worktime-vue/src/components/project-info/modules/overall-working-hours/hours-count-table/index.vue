<template>
	<div class="hours-count-table flex-1" h="326px">
		<div class="hours-count-table__header flex" h="42px">
			<div class="hours-count-table__header__item" w="70px">单位/人天</div>
			<div
				class="hours-count-table__header__item flex-1"
				v-for="(item, index) in tableRect.headers"
				:class="{ select: tableRect.selectIndex == index }"
				:key="index"
				@click="handleHeaderSelect(item, index)"
				h="100%"
			>
				{{ item.name }}
				<div class="flex flex-col justify-center items-center m-l-2px">
					<ElIcon size="10px" class="m-b--4px" :style="{ color: item.order == 1 ? '#1B86FF' : '#222' }">
						<CaretTop />
					</ElIcon>
					<ElIcon size="10px" :style="{ color: item.order == 2 ? '#1B86FF' : '#222' }">
						<CaretBottom />
					</ElIcon>
				</div>
			</div>
		</div>
		<div class="hours-count-table__body" h="258px">
			<template v-if="tableRect.dataSource.length > 0">
				<div
					class="hours-count-table__body__cell flex"
					v-for="(item, index) in tableRect.dataSource"
					:key="item.name"
					h="43px"
				>
					<div class="hours-count-table__body__cell__item name" w="70px">{{ item.name }}</div>
					<div class="hours-count-table__body__cell__item flex-1">
						{{ item[tableRect.headers[0].key] }}
					</div>
					<div class="hours-count-table__body__cell__item flex-1">
						{{ item[tableRect.headers[1].key] }}
					</div>
					<div class="hours-count-table__body__cell__item flex-1">
						{{ item[tableRect.headers[2].key] }}
					</div>
					<div class="hours-count-table__body__cell__item flex-1">
						{{ item[tableRect.headers[3].key] }}
					</div>
				</div>
			</template>
			<ElEmpty v-else h="258px" />
		</div>
	</div>
</template>

<script setup lang="ts">
	import { reactive } from "vue";
	import { CaretTop, CaretBottom } from "@element-plus/icons-vue";
	import { array } from "vue-types";

	const props = defineProps({
		list: array().def([])
	});

	const tableRect = reactive({
		headers: [
			{
				order: 0,
				name: "合计",
				key: "sumWorkHoursTotal"
			},
			{
				order: 0,
				name: "任务",
				key: "sumWorkHoursTask"
			},
			{
				order: 0,
				name: "运维",
				key: "sumWorkHoursOperation"
			},
			{
				order: 0,
				name: "修复",
				key: "sumWorkHoursTestBug"
			}
		],
		selectIndex: -1,
		dataSource: [...props.list]
	});

	const handleHeaderSelect = (item: any, index: number) => {
		const order = item.order;
		tableRect.headers.forEach((item) => {
			item.order = 0;
		});
		tableRect.selectIndex = index;
		const key = tableRect.headers[index].key;
		if (order == 0) {
			item.order = 1;
			tableRect.dataSource.sort((a, b) => {
				return a[key] - b[key];
			});
		} else if (order == 1) {
			item.order = 2;
			tableRect.dataSource.sort((a, b) => {
				return b[key] - a[key];
			});
		} else {
			item.order = 0;
			tableRect.dataSource = [...props.list];
		}
	};
</script>

<style lang="scss" scoped>
	.hours-count-table {
		background-image: url(/icon/project-info/icon-total-bg.png);
		background-size: 100% 100%;
		border-radius: 8px;
		overflow: hidden;
		padding: 12px;
		&__header {
			border-top-left-radius: 10px;
			border-top-right-radius: 10px;

			background-color: #fff;

			box-shadow: 0px -1px 3px 0px rgba(0, 0, 0, 0.12);
			overflow: hidden;
			cursor: pointer;

			&__item {
				font-weight: normal;
				font-size: 14px;
				color: rgba(0, 0, 0, 0.65);
				display: flex;
				align-items: center;
				justify-content: center;
				border-right: 1px solid rgba(0, 0, 0, 0.06);
				box-sizing: border-box;
				&:last-child {
					border-right: none;
				}
				&.select {
					color: #1b86ff;
					background: linear-gradient(180deg, #ffffff 0%, #d7edff 100%);
				}
			}
		}
		&__body {
			background: linear-gradient(180deg, #ffffff 51%, #f2f8ff 100%);
			box-shadow: 0px 0px 2px 3px rgba(46, 119, 221, 0.08);
			border-radius: 0px 0px 12px 12px;
			overflow: hidden;
			overflow-y: auto;
			&__cell {
				border-bottom: 1px dashed rgba(0, 0, 0, 0.06);
				&:last-child {
					border-bottom: none;
				}
				&__item {
					font-weight: normal;
					font-size: 14px;
					line-height: 42px;
					text-align: center;
					border-left: 1px solid transparent;
					border-right: 1px solid transparent;
					&.name {
						font-size: 14px;
						color: #344e6b;
						font-weight: bold;
					}
					&.selectCol {
						background: rgba(27, 134, 255, 0.08);
						border-left: 1px solid rgba(27, 134, 255, 0.13);
						border-right: 1px solid rgba(27, 134, 255, 0.13);
						&:last-child {
						}
					}
				}
			}
		}
	}
</style>
