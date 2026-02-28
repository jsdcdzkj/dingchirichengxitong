<template>
	<div class="m-t-20px">
		<ElDescriptions :column="2">
			<ElDescriptionsItem label="任务开始时间："
				>里程碑</ElDescriptionsItem
			>
			<ElDescriptionsItem label="任务完成时间："
				>里程碑</ElDescriptionsItem
			>
			<ElDescriptionsItem label="任务延期情况："
				>里程碑</ElDescriptionsItem
			>
			<ElDescriptionsItem label="复测次数：">里程碑</ElDescriptionsItem>
			<ElDescriptionsItem label="复测解决类型：" :span="2"
				>里程碑</ElDescriptionsItem
			>

			<ElDescriptionsItem label="任务描述：" :span="2"
				>里程碑</ElDescriptionsItem
			>
		</ElDescriptions>
	</div>
</template>
