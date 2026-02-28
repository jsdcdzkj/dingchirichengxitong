<template>
	<div class="flex-shrink-0" w="406px">
		<Title title="工时对比表" />
		<div class="abs-table m-t-20px">
			<div class="abs-table__head">
				<div class="abs-table__head__item relative" w="125px">
					<img
						w="49px"
						h="49px"
						class="absolute top--10px left-15px"
						src="/icon/project-info/icon-abs-money.png"
						alt=""
						srcset=""
					/>
				</div>
				<div class="abs-table__head__item" w="76px">
					<span class="abs-table__head__item__name">预估工时</span>
					<span class="abs-table__head__item__unit">(人天)</span>
				</div>
				<div class="abs-table__head__item" w="76px">
					<span class="abs-table__head__item__name">实际工时</span>
					<span class="abs-table__head__item__unit">(人天)</span>
				</div>
				<div class="abs-table__head__item" w="110px">
					<span class="abs-table__head__item__name">项目开发成本</span>
					<span class="abs-table__head__item__unit">(元)</span>
				</div>
			</div>
			<div class="abs-table__body">
				<div class="abs-table__body__item" v-for="item in list" :key="item.name">
					<div class="abs-table__body__item__cell cell-1" w="125px">
						{{ item.name }}
						<!-- <ElIcon style="color: #c0d3ea"><QuestionFilled /></ElIcon> -->
					</div>
					<div class="abs-table__body__item__cell item-color" w="76px">{{ item.planing_hours }}</div>
					<div class="abs-table__body__item__cell item-color" w="76px">{{ item.working_hours }}</div>
					<div class="abs-table__body__item__cell item-color" w="110px">{{ item.project_cost }}</div>
				</div>
			</div>
		</div>
	</div>
</template>
<script setup lang="ts">
	import { workHoursContrastData } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { QuestionFilled } from "@element-plus/icons-vue";
	import { ref } from "vue";
	const list = ref<any>([]);
	workHoursContrastData({ project_id: appInstance.projectItem.id }).then((res) => {
		Object.keys(res).forEach((key) => {
			list.value.push(res[key]);
		});
	});
</script>
<style lang="scss" scoped>
	.abs-table {
		&__head {
			display: flex;
			gap: 6px;

			&__item {
				background: #e6f2ff;
				flex-shrink: 0;
				text-align: center;
				height: 48px;
				border-radius: 8px 8px 8px 8px;
				font-size: 14px;
				color: #275798;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				&__name {
					font-weight: 600;
				}
				&__unit {
					font-weight: 400;
					font-size: 12px;
					color: #275798;
				}
			}
		}
		&__body {
			display: flex;
			flex-direction: column;
			gap: 6px;
			margin-top: 6px;
			&__item {
				display: flex;
				gap: 6px;
				&__cell {
					background: #e6f2ff;
					height: 34px;
					border-radius: 8px 8px 8px 8px;
					font-size: 14px;
					color: rgba(0, 0, 0, 0.85);
					font-weight: bold;
					text-align: center;
					line-height: 34px;
					&.cell-1 {
						color: #275798;
						display: flex;
						align-items: center;
						justify-content: center;
						gap: 4px;
					}
					&.item-color {
						background: #F4F7FF;
					}
				}
			}
		}
	}
</style>
