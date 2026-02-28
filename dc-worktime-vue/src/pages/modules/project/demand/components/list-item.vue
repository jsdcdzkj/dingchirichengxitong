<template>
	<div class="flex flex-items-center list" :class="{active: item.id == activeId}">
		<div class="flex flex-items-center list-item">
			<div class="name">{{ item.personChargeName }}</div>
			<ElTag class="m-l-8px" :type="item.priority == 2 ? 'danger' : 'primary'">{{ item.priorityLabel}}</ElTag>
			<div class="tip">{{ item.planName || '未设置' }}</div>
			<ElIcon class="icon"><Opportunity /></ElIcon>
			<div class="content">{{item.title}}</div>
		</div>
		<ElTag round :type="listTag[item.state].type" plain>{{ listTag[item.state].name }}</ElTag>
	</div>
	
</template>

<script setup lang="tsx">
import {
	Opportunity
} from "@element-plus/icons-vue";

const props = defineProps({
	activeId: {
		type: String,
		default: '7'
	},
	item: {
		type: Array,
		default: () => ([])
	},
});

const listTag = [{},
	{
		name:'未开始',
		type:'warning'
	},
	{
		name:'进行中',
		type:'primary'
	},
	{
		name:'已完成',
		type:'success'
	},
	{
		name:'已作废',
		type:'info'
	},
]

</script>
<style lang="scss" scoped>
.list {
	position: relative;
	width: 100%;
	border-bottom: 1px solid rgba(0,0,0,0.06);
	justify-content: space-between;
	padding: 0 24px;
}
.list-item {
	height: 56px;
	border-radius: 0px 0px 0px 0px;
	font-size: 14px;
	color: rgba(0,0,0,0.65);
	width: 86%;

	&::before {
		content: '';
		width: 2px;
		height: 56px;
		background: #fff;
		position: absolute;
		top:0;
		left:0;
		z-index:1;
	}

	.name {
		width: 58px;
		height: 24px;
		line-height:24px;
		text-align: center;
		background: #F8F8F8;
		border-radius: 4px 4px 4px 4px;
	}
	.icon {
		width: 28px;
		height: 20px;
		background: #F5A250;
		border-radius: 3px 3px 3px 3px;
		color: #fff;
		margin-left:8px;
	}

	.tip {
		padding: 0 8px;
		height: 24px;
		line-height: 24px;
		background: #F8F8F8;
		border-radius: 4px 4px 4px 4px;
		margin-left:8px;
	}

	.content {
		margin-left: 8px;
		flex:1;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		text-align: left;
	}

}
.active {
	background: rgba(27,134,255,0.04);
	&::before {
		content: '';
		width: 2px;
		height: 56px;
		position: absolute;
		top:0;
		left:0;
		background: #1B86FF;
		z-index:2;
	}
}
</style>