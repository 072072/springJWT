package sang.ik.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sang.ik.dto.member.MemberDto;
import sang.ik.exception.MemberNotFoundException;
import sang.ik.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto read(Long id){
        return MemberDto.toDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }


    /**
     *  deleteById 는 내부적으로 delete 메소드를 호출 findById를 수행한 뒤 delete메소드에 인자로 넘겨줌.
     *
     *  현재 검증로직에서 findById 한번 , delete에서 findById 한번 총 두번의 select 쿼리가 나가는데 나중에 수정할 예정
     */
    @Transactional
    public void delete(Long id){
        if(notExistsMember(id)){
            throw new MemberNotFoundException();
        }
        memberRepository.deleteById(id);
    }

    private boolean notExistsMember(Long id){
        return !memberRepository.existsById(id);
    }
}
