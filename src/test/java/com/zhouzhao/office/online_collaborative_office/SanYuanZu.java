package com.zhouzhao.office.online_collaborative_office;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SanYuanZu {
    public static void main(String[] args) {
        System.out.println(threeSum(new int[]{-1, 0, 1, 2, -1, -4}));

        System.out.println(opt(new int[]{-1, 0, 1, 2, -1, -4}));
    }

    public static List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        // 枚举 a
        for (int i = 0; i < n; ++i) {
            // 需要和上一次枚举的数不相同
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            // c 对应的指针初始指向数组的最右端
            int right = n - 1;
            int target = -nums[i];
            // 枚举 b
            for (int left = i + 1; left < n; ++left) {
                // 需要和上一次枚举的数不相同
                if (left > i + 1 && nums[left] == nums[left - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (left < right && nums[left] + nums[right] > target) {
                    --right;
                }
                // 如果指针重合，随着 b 后续的增加
                // 就不会有满足 a+b+c=0 并且 b<c 的 c 了，可以退出循环
                if (left == right) {
                    break;
                }
                if (nums[left] + nums[right] == target) {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(nums[i]);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    ans.add(list);
                }
            }
        }
        return ans;
    }


    public static List<List<Integer>> opt(int[] nums) {
        int len = nums.length;
        Arrays.sort(nums);
        ArrayList<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }

            int target = -nums[i];
            int right = len - 1;
            for (int left = i + 1; left < len; left++) {
                if (left > i + 1 && nums[left] == nums[left - 1]) {
                    continue;
                }
                while (left < right && nums[left] + nums[right] > target) {
                    right--;
                }
                if (left == right) {
                    continue;
                }
                if (nums[left] + nums[right] == target) {
                    ArrayList<Integer> integers = new ArrayList<>();
                    integers.add(nums[i]);
                    integers.add(nums[left]);
                    integers.add(nums[right]);
                    lists.add(integers);
                }
            }


        }


        return lists;
    }
}
