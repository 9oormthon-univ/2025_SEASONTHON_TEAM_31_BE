package com.cloudtone31.mission.service;

import com.cloudtone31.mission.domain.Answers;
import com.cloudtone31.mission.domain.Missions;

import com.cloudtone31.mission.repository.AnswerRepository;
import com.cloudtone31.mission.repository.MissionRepository;
import com.cloudtone31.user.domain.User;
import com.cloudtone31.user.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 자동으로 만들어주는 Lombok 어노테이션
public class MissionService {

    // 이전에 만든 Repository들을 주입받습니다.
    private final MissionRepository missionRepository;
    private final AnswerRepository answerRepository;
    private final UserLoginRepository userRepository; // 답변을 저장할 때 유저 정보가 필요하므로 추가합니다.
    // private final UserPlantRepository userPlantRepository; // 나중에 식물 성장 로직을 위해 추가할 예정

    /**
     * 오늘의 미션을 조회하는 핵심 로직입니다.
     * @param userId 현재 로그인한 사용자의 ID
     * @param condition 사용자가 선택한 오늘의 컨디션
     * @return 랜덤으로 선정된 미션 2개
     */
    @Transactional(readOnly = true) // 데이터를 변경하지 않는 조회 기능이므로 readOnly = true로 성능을 최적화합니다.
    public List<Missions> getDailyMissions(Long userId, String condition) {
        // 1. 사용자가 이미 답변한 미션들의 ID 목록을 가져옵니다.
        List<Long> answeredMissionIds = answerRepository.findAnsweredMissionIdsByUserId(userId);

        // 2. 답변하지 않은 미션들만 DB에서 조회합니다.
        List<Missions> unansweredMissions;
        if (answeredMissionIds.isEmpty()) {
            // 답변한 미션이 하나도 없으면, 굳이 NOT IN 쿼리를 쓸 필요가 없습니다.
            unansweredMissions = missionRepository.findByCondition(condition);
        } else {
            unansweredMissions = missionRepository.findMissionsByConditionAndIdNotIn(condition, answeredMissionIds);
        }

        // 3. 만약 답변하지 않은 미션이 하나도 없다면 (해당 컨디션의 미션을 모두 다 했다면),
        // 그냥 해당 컨디션의 모든 미션 중에서 랜덤으로 보여줍니다. (정책에 따라 변경 가능)
        if (unansweredMissions.isEmpty()) {
            unansweredMissions = missionRepository.findByCondition(condition);
        }

        // 4. 조회된 미션 목록을 랜덤하게 섞습니다.
        Collections.shuffle(unansweredMissions);

        // 5. 섞인 목록에서 최대 2개를 잘라서 반환합니다. (미션이 1개만 있을 수도 있으니 Math.min 사용)
        return unansweredMissions.subList(0, Math.min(unansweredMissions.size(), 2));
    }

    /**
     * 사용자가 제출한 답변을 저장하는 로직입니다.
     * @param userId 답변을 제출한 사용자 ID
     * @param missionId 답변한 미션의 ID
     * @param content 답변 내용
     */
    @Transactional
    public Answers submitAnswer(Long userId, Long missionId, String content) { // 반환 타입을 Answers로 변경
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));
        Missions mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 미션을 찾을 수 없습니다. id=" + missionId));

        Answers newAnswer = Answers.createAnswer(user, mission, content);

        // save 메소드는 저장된 객체를 반환하므로, 이를 그대로 return해줍니다.
        return answerRepository.save(newAnswer);
    }
}