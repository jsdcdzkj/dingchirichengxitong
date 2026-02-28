<template>
	<div class="m-t-20px">
		<div class="flex">
			<!-- 这边是左侧的菜单列表 -->
			<el-menu
				v-model="activeIndex"
				class="el-menu-vertical-demo w-200px"
				:default-active="activeIndex"
				@select="handleSelect"
			>
				<el-menu-item index="1">选项1</el-menu-item>
				<el-menu-item index="2">选项2</el-menu-item>
				<el-menu-item index="3">选项3</el-menu-item>
			</el-menu>
			<!-- 这边是右侧的内容 -->
			<el-container class="main-con">
				<div class="change-title"></div>
				<ElDescriptions :column="1">
					<ElDescriptionsItem label="被指派人：" :span="3" label-class-name="bold-text" class-name="bold-text"
						>XXXX变更为XXXX</ElDescriptionsItem
					>
					<ElDescriptionsItem label="补充说明：">里程碑</ElDescriptionsItem>
					<ElDescriptionsItem label="截止日期变更：">里程碑</ElDescriptionsItem>
					<ElDescriptionsItem label="变更人：">里程碑</ElDescriptionsItem>
				</ElDescriptions>
			</el-container>
		</div>
	</div>
</template>
<script setup lang="ts">
	import { ref, onMounted, reactive, toRaw } from "vue";
	import { ElRow, ElCol, ElDescriptions, ElDescriptionsItem, ElMenu, ElMenuItem, ElContainer } from "element-plus";

	const activeIndex = ref("1");

	const handleSelect = (index: string) => {
		activeIndex.value = index;
	};
</script>

<style>
	.main-con {
		padding-left: 20px;
		flex: 1;
		min-height: 400px;
		border-left: 1px solid rgba(0, 0, 0, 0.06);
	}
	.bold-text {
		font-weight: bold;
	}
</style>
