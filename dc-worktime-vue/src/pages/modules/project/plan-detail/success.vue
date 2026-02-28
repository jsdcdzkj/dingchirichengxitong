<template>
	<ElCard shadow="never">
		<ElDescriptions :column="1">
			<ElDescriptionsItem label="任务开始时间">{{ actual_start_date || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务结束时间">{{ actual_end_date || "--" }}</ElDescriptionsItem>
			<ElDescriptionsItem label="任务延期情况">--</ElDescriptionsItem>
		</ElDescriptions>
	</ElCard>
</template>

<script lang="tsx" setup>
	import { ref, watchEffect } from "vue";
	import { number, string } from "vue-types";
	import { ElCard, ElDescriptionsItem, ElDescriptions } from "element-plus";

	const props = defineProps({
		actual_start_date: number().def(),
		actual_end_date: string().def()
	});
	const description = ref(props.plan_desc);
	watchEffect(() => {
		// props.plan_desc && (description.value = props.plan_desc);
		description.value = props.plan_desc;
	});

	const emit = defineEmits(["update"]);
	const handleBlur = () => {
		emit("update", description.value);
	};
</script>
