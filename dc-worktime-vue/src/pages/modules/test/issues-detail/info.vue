<template>
	<div class="m-t-20px">
		<ElDescriptions :column="3">
			<ElDescriptionsItem label="BUG提出人：">{{ formattedDataSource.createUserName }}</ElDescriptionsItem>
			<ElDescriptionsItem label="严重程度：">{{ formattedDataSource.severityName }}</ElDescriptionsItem>
			<ElDescriptionsItem label="提出时间：">{{ formattedDataSource.createTime }}</ElDescriptionsItem>
			<ElDescriptionsItem label="BUG来源：">{{ formattedDataSource.sourceName }}</ElDescriptionsItem>
			<ElDescriptionsItem label="评审名称：" :span="2">里程碑</ElDescriptionsItem>
			<ElDescriptionsItem label="所属项目：">{{ formattedDataSource.pro_project_name }}</ElDescriptionsItem>
			<ElDescriptionsItem label="所属模块：">{{ formattedDataSource.moduleName }}</ElDescriptionsItem>
			<ElDescriptionsItem label="功能名称：">{{ formattedDataSource.featureName }}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务描述：" :span="3" class="flex" class-name="des-box">
				<div class="desc-img" v-html="formattedDataSource.description"></div>
			</ElDescriptionsItem>
		</ElDescriptions>
	</div>
</template>
<script setup lang="tsx">
	import { defineProps, computed } from "vue";

	const props = defineProps({
		dataSource: {
			type: [Object, Array], // 根据实际数据类型调整
			default: () => {}
		}
	});

	// 定义一个函数来处理数据为空的情况
	const getDefaultValue = (value) => value || "--";

	// 使用计算属性处理数据
	const formattedDataSource = computed(() => {
		const source = props.dataSource || {};
		return {
			createUserName: getDefaultValue(source.create_user_name),
			severityName: getDefaultValue(source.severityName),
			createTime: getDefaultValue(source.createTime),
			description: getDefaultValue(source.description),
			sourceName: getDefaultValue(source.sourceName),
			pro_project_name: getDefaultValue(source.pro_project_name),
			moduleName: getDefaultValue(source.moduleName),
			featureName: getDefaultValue(source.featureName)
		};
	});
</script>
<style scoped lang="scss">
	:deep(.el-descriptions__label) {
		display: inline-block;
		width: 86px;
	}
	:deep(.el-descriptions__label:not(.is-bordered-label)) {
		margin-right: 6px;
	}
	:deep(.des-box) {
		display: inline-block;
		width: calc(100% - 92px);
		p {
			margin: 0;
		}
	}
</style>
