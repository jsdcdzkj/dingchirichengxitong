<template>
	<ElCard shadow="never">
		<div class="flex flex-items-center">
			<img w="24px" h="24px" src="../../../../assets/milestone/icon-tag.png" alt="" srcset="" />
			<span class="m-l-10px">描述</span>
		</div>
		<div class="m-t-20px">
			<Editor
				style="height: 300px"
				contentType="html"
				:options="{ readOnly: true, placeholder: '' }"
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
				<ElDescriptionsItem label="类型">里程碑</ElDescriptionsItem>
				<ElDescriptionsItem label="创建时间：">{{ createTime }}</ElDescriptionsItem>
				<ElDescriptionsItem label="更新时间：">{{ updateTime }}</ElDescriptionsItem>
				<ElDescriptionsItem label="创建者">{{ createUsername }}</ElDescriptionsItem>
				<ElDescriptionsItem label="关联计划：">{{ planName }}</ElDescriptionsItem>
			</ElDescriptions>
		</div>
	</ElCard>
</template>

<script setup lang="ts">
	import { saveOrUpd } from "@/api/project/milestone";
	import { ref } from "vue";
	import { string } from "vue-types";
	import { ElDescriptions, ElDescriptionsItem } from "element-plus";

	const props = defineProps({
		createTime: string().def(),
		createUsername: string().def(),
		updateTime: string().def(),
		planName: string().def(),
		description: string().def(),
		id: string().def()
	});

	const description = ref(props.description);
	const emit = defineEmits(["update"]);

	const handleBlur = () => {
		saveOrUpd({ id: props.id, description: description.value }).then(() => {
			emit("update");
		});
	};
</script>
