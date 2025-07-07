package shop.wannab.book_service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.wannab.book_service.category.entity.Category;
import shop.wannab.book_service.category.repository.CategoryRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        // DB에 카테고리 데이터가 비어있을 때만 실행
        if (categoryRepository.count() == 0) {
            log.info(">>> DB에 카테고리 초기 데이터를 생성합니다.");
            initializeCategories();
        } else {
            log.info(">>> 카테고리 데이터가 이미 존재하여 초기화를 건너뜁니다.");
        }
    }

    @Transactional
    public void initializeCategories() {
        // --- 최상위 카테고리 생성 ---
        Category novel = createCategory("소설/시/희곡", null);
        Category essay = createCategory("에세이", null);
        Category management = createCategory("경제경영", null);
        Category selfDev = createCategory("자기계발", null);
        Category humanities = createCategory("인문학", null);
        Category history = createCategory("역사", null);
        Category society = createCategory("사회과학", null);
        Category science = createCategory("과학", null);
        Category art = createCategory("예술/대중문화", null);
        Category religion = createCategory("종교/역학", null);
        Category kids = createCategory("어린이", null);
        Category toddler = createCategory("유아", null);
        Category parenting = createCategory("좋은부모", null);
        Category teen = createCategory("청소년", null);
        Category cooking = createCategory("요리/살림", null);
        Category hobby = createCategory("건강/취미", null);
        Category travel = createCategory("여행", null);
        Category language = createCategory("외국어", null);
        Category computer = createCategory("컴퓨터/모바일", null);
        Category comics = createCategory("만화", null);
        Category magazine = createCategory("잡지", null);
        Category exam = createCategory("수험서/자격증", null);
        Category university = createCategory("대학교재", null);
        Category reference = createCategory("참고서", null);
        Category collection = createCategory("전집/중고전집", null);
        Category etc = createCategory("달력/기타", null);

        // --- 하위 카테고리 생성 ---

        // 소설/시/희곡
        createCategory("한국소설", novel);
        createCategory("일본소설", novel);
        createCategory("중국소설", novel);
        createCategory("영미소설", novel);
        createCategory("유럽소설", novel);
        createCategory("기타 나라 소설", novel);
        createCategory("고전소설", novel);
        createCategory("장르소설", novel);
        createCategory("시", novel);
        createCategory("희곡", novel);
        createCategory("세계문학전집", novel);

        // 에세이
        createCategory("한국에세이", essay);
        createCategory("외국에세이", essay);
        createCategory("인물/평전", essay);
        createCategory("명상/치유 에세이", essay);

        // 경제경영
        createCategory("경제학/경제일반", management);
        createCategory("경영일반", management);
        createCategory("마케팅/세일즈", management);
        createCategory("CEO/비즈니스맨", management);
        createCategory("재테크/투자", management);
        createCategory("성공학", management);
        createCategory("주식/증권", management);
        createCategory("기업 경영/리더십", management);
        createCategory("e-비즈니스", management);

        // 자기계발
        createCategory("성공/처세", selfDev);
        createCategory("자기능력계발", selfDev);
        createCategory("인간관계", selfDev);
        createCategory("화술/협상", selfDev);
        createCategory("여성", selfDev);
        createCategory("비즈니스 능력", selfDev);
        createCategory("시간관리", selfDev);

        // 인문학
        createCategory("인문학일반", humanities);
        createCategory("심리학", humanities);
        createCategory("교육학", humanities);
        createCategory("철학", humanities);
        createCategory("문학이론", humanities);
        createCategory("언어학", humanities);
        createCategory("독서/글쓰기", humanities);
        createCategory("성과 문화", humanities);
        createCategory("정치/사회 비평", humanities);

        // 역사
        createCategory("역사일반", history);
        createCategory("한국사", history);
        createCategory("동양사", history);
        createCategory("서양사", history);
        createCategory("세계사", history);
        createCategory("신화", history);
        createCategory("역사인물", history);

        // 사회과학
        createCategory("정치/사회", society);
        createCategory("법학", society);
        createCategory("언론/미디어", society);
        createCategory("사회문제/복지", society);
        createCategory("여성학/젠더", society);
        createCategory("인권", society);

        // 과학
        createCategory("기초과학", science);
        createCategory("교양과학", science);
        createCategory("의학", science);
        createCategory("공학", science);
        createCategory("IT 모바일", science);
        createCategory("생명과학", science);
        createCategory("지구과학", science);

        // 예술/대중문화
        createCategory("예술일반", art);
        createCategory("미술", art);
        createCategory("음악", art);
        createCategory("건축", art);
        createCategory("사진", art);
        createCategory("영화", art);
        createCategory("연극/뮤지컬", art);
        createCategory("디자인", art);
        createCategory("만화/애니메이션", art);
        createCategory("대중문화/비평", art);

        // 종교/역학
        createCategory("종교일반", religion);
        createCategory("기독교(개신교)", religion);
        createCategory("천주교(가톨릭)", religion);
        createCategory("불교", religion);
        createCategory("기타 종교", religion);
        createCategory("역학/사주", religion);

        // 어린이
        createCategory("유아", kids);
        createCategory("어린이 문학", kids);
        createCategory("어린이 교양", kids);
        createCategory("어린이 학습", kids);
        createCategory("어린이 영어", kids);
        createCategory("초등 참고서", kids);

        // 유아
        createCategory("그림책", toddler);
        createCategory("놀이책", toddler);
        createCategory("학습/인지", toddler);
        createCategory("생활/습관", toddler);
        createCategory("0-3세", toddler);
        createCategory("4-7세", toddler);

        // 좋은부모
        createCategory("자녀교육", parenting);
        createCategory("임신/출산/육아", parenting);
        createCategory("가족/부부", parenting);

        // 청소년
        createCategory("청소년 문학", teen);
        createCategory("청소년 교양", teen);
        createCategory("청소년 자기계발", teen);
        createCategory("청소년 학습법", teen);

        // 요리/살림
        createCategory("요리일반", cooking);
        createCategory("건강요리", cooking);
        createCategory("생활요리", cooking);
        createCategory("와인/커피/음료", cooking);
        createCategory("인테리어/DIY", cooking);

        // 건강/취미
        createCategory("건강일반", hobby);
        createCategory("스포츠/레저", hobby);
        createCategory("뷰티/패션", hobby);
        createCategory("취미/실용", hobby);
        createCategory("게임", hobby);
        createCategory("반려동물", hobby);

        // 여행
        createCategory("국내여행", travel);
        createCategory("해외여행", travel);
        createCategory("테마여행", travel);

        // 외국어
        createCategory("영어", language);
        createCategory("일본어", language);
        createCategory("중국어", language);
        createCategory("기타 외국어", language);
        createCategory("어학시험", language);

        // 컴퓨터/모바일
        createCategory("컴퓨터일반", computer);
        createCategory("OS", computer);
        createCategory("프로그래밍", computer);
        createCategory("네트워크/보안", computer);
        createCategory("데이터베이스", computer);
        createCategory("웹/모바일", computer);
        createCategory("게임", computer);
        createCategory("자격증", computer);

        // 만화
        createCategory("교양만화", comics);
        createCategory("순정만화", comics);
        createCategory("명랑/코믹", comics);
        createCategory("SF/판타지", comics);
        createCategory("공포/추리", comics);
        createCategory("액션/무협", comics);
        createCategory("성인만화", comics);
        createCategory("BL/GL", comics);

        // 잡지
        createCategory("주간/월간지", magazine);
        createCategory("계간/연간", magazine);
        createCategory("해외잡지", magazine);

        // 수험서/자격증
        createCategory("공무원", exam);
        createCategory("자격증", exam);
        createCategory("국가고시", exam);
        createCategory("취업/상식", exam);
        createCategory("운전면허", exam);

        // 대학교재
        createCategory("전공도서", university);
        createCategory("교양도서", university);

        // 참고서
        createCategory("초등참고서", reference);
        createCategory("중등참고서", reference);
        createCategory("고등참고서", reference);

        // 전집/중고전집
        createCategory("유아/어린이", collection);
        createCategory("청소년/성인", collection);

        // 달력/기타
        createCategory("달력/다이어리", etc);
        createCategory("가계부/용돈기입장", etc);
    }

    private Category createCategory(String name, Category parent) {
        Category category = new Category(name, parent);
        return categoryRepository.save(category);
    }
}