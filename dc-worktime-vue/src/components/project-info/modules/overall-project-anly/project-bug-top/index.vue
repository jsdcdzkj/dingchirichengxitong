<template>
	<div class="flex-1" h="323px">
		<Title title="最易出BUG的目录菜单排名top5" />
		<ul class="project-top flex flex-col items-center gap-6px" v-if="top5Items.length > 0">
			<li
				v-for="(item, index) in top5Items"
				:key="index"
				class="project-top__item flex justify-between w-full gap-6px"
			>
				<div class="project-top__item__index">{{ index + 1 }}</div>
				<div class="project-top__item__name" v-textEllipsis>{{ item.name }}</div>
				<div class="project-top__item__rank">
					{{ item.rank }}
				</div>
				<div class="project-top__item__realnames" v-textEllipsis>
					{{ item.realNameWithBug }}
				</div>
			</li>
		</ul>
		<ElEmpty v-else />
	</div>
</template>

<script lang="ts" setup>
	import { bugModelTop } from "@/api/project/info";
	import { appInstance } from "@/main";
	import { ref, computed } from "vue";

	interface MenuItem {
		name: string;
		rank: number;
	}

	const menuItems = ref<MenuItem[]>([]);

	const top5Items = computed(() => {
		return menuItems.value.sort((a, b) => b.rank - a.rank).slice(0, 5);
	});
	bugModelTop({ pro_project_id: appInstance.projectItem.id }).then((res) => {
		menuItems.value = res.map((item) => {
			return {
				name: item.moduleName,
				rank: item.bugNumber,
				realNameWithBug: item.realNameWithBug,
				realName: item.realName
			};
		});
	});
</script>

<style scoped lang="scss">
	h2 {
		color: #333;
	}

	ul {
		list-style-type: none;
		padding: 0;
	}

	li {
		padding: 5px 0;
	}
	.project-top {
		&__item {
			width: 100%;
			&__index {
				text-align: center;
				width: 39px;
				height: 39px;

				border-radius: 8px 8px 8px 8px;
				border: 1px solid rgba(0, 0, 0, 0.06);
				line-height: 39px;
				color: #fff;
				background-image: url(/icon/project-info/icon-btn-select.png);
				background-size: 100% 100%;
				overflow: hidden;
			}
			&__name {
				flex: 1;
				background: #f4f7ff;
				border-radius: 8px 8px 8px 8px;
				line-height: 39px;
				padding: 0 16px;
				height: 39px;
				line-height: 39px;
				text-align: left;
				font-size: 14px;
				color: rgba(0, 0, 0, 0.85);
				font-weight: bold;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
				width: 0;
				min-width: 0;
			}
			&__rank {
				text-align: center;
				width: 51px;
				height: 39px;
				background: #f4f7ff;
				border-radius: 8px 8px 8px 8px;
				line-height: 39px;
				color: rgba(42, 112, 191, 1);
			}
			&__realnames {
				text-align: center;
				width: 100px;
				height: 39px;
				padding: 0 10px;
				background: #f4f7ff;
				border-radius: 8px 8px 8px 8px;
				line-height: 39px;
				color: rgba(42, 112, 191, 1);
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
				font-size: 14px;
			}
		}
	}
</style>
