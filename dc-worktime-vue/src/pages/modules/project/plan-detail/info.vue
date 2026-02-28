<template>
	<ElCard shadow="never">
		<div class="flex flex-items-center">
			<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
			<span class="m-l-10px">描述</span>
		</div>
		<div class="m-t-20px">
			<Editor
				:options="{ readOnly: true, placeholder: '' }"
				style="height: 300px"
				contentType="html"
				v-model:model-value="description"
			></Editor>
		</div>
	</ElCard>
	<ElCard shadow="never" class="m-t-10px">
		<div class="flex flex-items-center">
			<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
			<span class="m-l-10px">属性</span>
		</div>
		<div class="m-t-20px">
			<ElDescriptions :column="3">
				<ElDescriptionsItem label="类型">{{ props.plan_type == 2 ? "计划" : "阶段" }}</ElDescriptionsItem>
				<ElDescriptionsItem label="创建时间">{{ props.createTime }}</ElDescriptionsItem>
				<ElDescriptionsItem label="更新时间">{{ props.updateTime }}</ElDescriptionsItem>
				<ElDescriptionsItem label="截至时间">{{ props.plan_end_date }}</ElDescriptionsItem>
				<ElDescriptionsItem label="创建者">{{ props.createUserName }}</ElDescriptionsItem>
			</ElDescriptions>
		</div>
	</ElCard>
</template>

<script lang="tsx" setup>
	import { ref, watchEffect } from "vue";
	import { number, string } from "vue-types";
	import { ElCard, ElDescriptionsItem, ElDescriptions } from "element-plus";

	const props = defineProps({
		createTime: string().def(),
		createUserName: string().def(),
		plan_type: number().def(),
		plan_end_date: string().def(),
		updateTime: string().def(),
		planName: string().def(),
		plan_desc: string().def(),
		id: string().def()
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
