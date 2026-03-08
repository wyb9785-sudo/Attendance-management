package com.test.sshProject.controller.admin;
//请假管理
import com.test.sshProject.dto.LeaveApplicationDTO;
import com.test.sshProject.entity.LeaveApplication;
import com.test.sshProject.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/leave")
public class AdminLeaveController {

    @Autowired
    private LeaveService leaveService;
    @Autowired
    private HttpServletRequest request;

    // 获取当前用户ID的方法
    private String getCurrentUserId() {
        HttpSession session = request.getSession();
        return (String) session.getAttribute("currentUserId"); // 根据实际Session键名调整
    }

    // 审批请假申请
    @PostMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveLeave(
            @RequestParam("leaveId") Integer leaveId,
            @RequestParam("status") String status,
            @RequestParam(value = "approveOpinion", required = false) String approveOpinion) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取当前登录管理员的ID
            String approverId = getCurrentUserId();
            if (approverId == null || approverId.isEmpty()) {
                throw new RuntimeException("无法获取当前审批人ID");
            }
            // 创建LeaveApplication对象并设置审批信息
            LeaveApplication leave = new LeaveApplication();
            leave.setLeaveId(leaveId);
            leave.setStatus(status);
            leave.setApproveOpinion(approveOpinion);
            leave.setApproverId(approverId);
            leave.setApproveTime(new java.sql.Timestamp(System.currentTimeMillis()));

            // 调用Service层方法
            leaveService.approveLeave(leave);

            result.put("success", true);
            result.put("message", "审批完成");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "审批失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
    // 查看所有请假申请
    @GetMapping("/list")
    public List<LeaveApplicationDTO> listLeaveApplications() {
        return leaveService.getAllLeaveApplicationsWithDetails();
    }


    // 查看待审批请假申请
    @GetMapping("/pending")
    public List<LeaveApplicationDTO> getPendingLeaveApplications(@RequestParam("type") String type) {
        if ("all".equals(type)) {
            return leaveService.getPendingLeaveApplicationsWithDetails();
        }
        return leaveService.getAllLeaveApplicationsByStatusAndTypeWithDetails("待审批", type);
    }

    // 查看所有请假记录
    @GetMapping("/all")
    public List<LeaveApplicationDTO> getAllLeaveRecords(@RequestParam("status") String status, @RequestParam("type") String type) {
        if ("all".equals(status) && "all".equals(type)) {
            return leaveService.getAllLeaveApplicationsWithDetails();
        }
        return leaveService.getAllLeaveApplicationsByStatusAndTypeWithDetails(status, type);
    }

    //查看请假详细信息
    @GetMapping("/details/{leaveId}")
    public LeaveApplicationDTO getLeaveDetails(@PathVariable String leaveId) {
        return leaveService.getLeaveDetails(leaveId);
    }
}