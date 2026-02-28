<template>
	<BasePage>
		<template #pageHeader>
			<TableHeader title="项目进度">
				<!-- <el-popover
					placement="top-start"
					title="Title"
					:width="200"
					trigger="hover"
					content="this is content, this is content, this is content"
				>
					<template #reference>
						<ElButton type="default" plain> 图例信息</ElButton>
					</template>
				</el-popover> -->
			</TableHeader>
		</template>
		<template #pageBody>
			<div id="canvas" ref="canvas"></div>
		</template>
	</BasePage>
</template>

<script setup lang="ts">
	import { projectProgress } from "@/api/project/list";
	import { TableRender } from "@/charts";
	import BasePage from "@/core/struct/page/base-page";
	import TableHeader from "@/core/struct/table/table-header";
	import { appInstance } from "@/main";
	import { onMounted, ref } from "vue";

	const getData = () => {
		return [
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-04-01",
						endDate: "2025-04-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			},
			{
				id: Math.random().toString(16).slice(2, 12),
				title: "项目立项",
				type: "1",
				startDate: "2025-03-18",
				endDate: "2025-03-24",
				progress: 80,
				children: [
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-1",
						type: "2",
						progress: 77,
						startDate: "2025-03-01",
						endDate: "2025-03-18",
						children: [
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-1",
								type: "3",
								progress: 90,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							},
							{
								id: Math.random().toString(16).slice(2, 12),
								title: "项目立项2-2",
								type: "3",
								progress: 40,
								startDate: "2025-02-20",
								endDate: "2025-03-08"
							}
						]
					},
					{
						id: Math.random().toString(16).slice(2, 12),
						title: "项目立项1-2",
						type: "3",
						progress: 66,
						startDate: "2025-01-20",
						endDate: "2025-03-08"
					}
				]
			}
		];
	};
	const canvas = ref<HTMLDivElement>();

	onMounted(() => {
		projectProgress({ project_id: appInstance.projectItem.id }).then((res) => {
			if (canvas.value) {
				new TableRender(canvas.value, res, {
					box: {
						cellHeight: 48,
						cellWidth: 48
					}
				}).render();
			}
		});
	});
</script>

<style lang="scss" scoped>
	#canvas {
		height: 100%;
		width: 100%;
		overflow: hidden;
		position: relative;
		border: 1px solid #eee;
		border-radius: 6px;
		overflow: hidden;
	}
</style>
