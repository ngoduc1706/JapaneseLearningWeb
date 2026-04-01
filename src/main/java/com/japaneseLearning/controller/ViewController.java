package com.japaneseLearning.controller;

import com.japaneseLearning.entity.Course;
import com.japaneseLearning.entity.UserCourseEnrollment;
import com.japaneseLearning.entity.Lesson;
import com.japaneseLearning.entity.Vocabulary;
import com.japaneseLearning.entity.Grammar;
import com.japaneseLearning.entity.Kanji;
import com.japaneseLearning.entity.KanjiRadical;
import com.japaneseLearning.entity.Payment;
import com.japaneseLearning.repository.CourseRepository;
import com.japaneseLearning.repository.GrammarRepository;
import com.japaneseLearning.repository.KanjiRepository;
import com.japaneseLearning.repository.KanjiRadicalRepository;
import com.japaneseLearning.repository.LessonRepository;
import com.japaneseLearning.service.EnrollmentService;
import com.japaneseLearning.service.CourseService;
import com.japaneseLearning.service.LessonService;
import com.japaneseLearning.service.MomoService;
import com.japaneseLearning.service.PaymentService;
import com.japaneseLearning.service.VocabularyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;


/**
 * ViewController handles view template routing for server-side rendering
 * Maps to GET requests that return HTML templates instead of JSON
 */
@Controller
@Transactional(readOnly = true)
public class ViewController {
    
    @Autowired
    private EnrollmentService enrollmentService;
    
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private GrammarRepository grammarRepository;

    @Autowired
    private KanjiRepository kanjiRepository;

    @Autowired
    private KanjiRadicalRepository kanjiRadicalRepository;

    @Autowired
    private MomoService momoService;

    @Autowired
    private PaymentService paymentService;

    /**
     * Home page - redirects to dashboard if authenticated, otherwise shows index
     */
    @GetMapping("/")
    public String home(Model model) {
        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            // User is authenticated, redirect to dashboard
            return "redirect:/dashboard";
        }
        // User is not authenticated, show index page with available courses
        try {
            var allCourses = courseService.getAllCourses();
            model.addAttribute("courses", allCourses);
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        model.addAttribute("pageTitle", "Japanese Learning Web - Home");
        return "index";
    }

    /**
     * Courses list page
     */
    @GetMapping("/courses")
    public String coursesList(Model model) {
        try {
            List<Course> courses = courseRepository.findAll();
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        model.addAttribute("pageTitle", "Khóa học - Japanese Learning");
        return "course/list";
    }

    /**
     * Course detail page
     */
    @GetMapping("/courses/{courseId}")
    public String courseDetail(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        Course course = courseRepository.findById(courseId).orElse(null);
        model.addAttribute("course", course);
        model.addAttribute("pageTitle", (course != null ? course.getTitle() : "Chi tiết khóa học"));
        return "course/detail";
    }

    /**
     * Checkout / Enrollment page for a course
     * - Free + Already enrolled  → redirect to lessons immediately
     * - Paid + Already enrolled  → show checkout with "go to course" option
     * - Not enrolled (any price) → show full checkout/payment form
     */
    @GetMapping("/courses/{courseId}/checkout")
    public String checkoutPage(@PathVariable Long courseId, Model model) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));

            boolean isEnrolled = isUserAuthorizedForCourse(courseId);
            boolean isFree = course.isIsFree();

            // Only auto-redirect for FREE courses where user is ALREADY enrolled
            if (isEnrolled && isFree) {
                return "redirect:/lessons/course/" + courseId;
            }

            model.addAttribute("course", course);
            model.addAttribute("isAlreadyEnrolled", isEnrolled);
            model.addAttribute("lessonCount", lessonRepository.countByCourse_CourseId(courseId));
            model.addAttribute("priceFormatted", formatPrice(course.getPrice(), isFree));
            model.addAttribute("pageTitle", "Đăng ký khóa học - " + course.getTitle());
        } catch (Exception e) {
            return "redirect:/courses";
        }
        return "course/checkout";
    }

    /**
     * Step 2 — QR scan page
     */
    @GetMapping("/courses/{courseId}/checkout/qr")
    public String checkoutQrPage(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long amount,
            Model model) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));
            String orderId = "MM" + System.currentTimeMillis() % 10000000000L;
            String qrData  = java.net.URLEncoder.encode("https://momo.vn/pay?courseId=" + courseId + "&orderId=" + orderId, "UTF-8");
            String qrUrl   = "https://api.qrserver.com/v1/create-qr-code/?data=" + qrData + "&size=160x160&ecc=M&margin=4";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "Khách";
            boolean isFree  = course.isIsFree();
            // Use discounted amount from URL if provided (voucher was applied)
            double basePrice = (course.getPrice() != null) ? course.getPrice() : 0;
            double finalAmt  = (amount != null && amount >= 0) ? amount : basePrice;
            model.addAttribute("course", course);
            model.addAttribute("courseId", courseId);
            model.addAttribute("lessonCount", lessonRepository.countByCourse_CourseId(courseId));
            model.addAttribute("orderId", orderId);
            model.addAttribute("qrUrl", qrUrl);
            model.addAttribute("username", username);
            model.addAttribute("amount", (long) finalAmt);
            model.addAttribute("priceFormatted", formatPrice(finalAmt, isFree || finalAmt == 0));
        } catch (Exception e) {
            return "redirect:/courses";
        }
        return "course/checkout-qr";
    }

    /**
     * Step 3 — Payment success page (also enrolls the user)
     */
    @GetMapping("/courses/{courseId}/checkout/success")
    @org.springframework.transaction.annotation.Transactional
    public String checkoutSuccessPage(
            @PathVariable Long courseId,
            @RequestParam(required = false, defaultValue = "MM00000000") String txId,
            @RequestParam(required = false, defaultValue = "0") Double amount,
            Model model) {
        try {
            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                enrollmentService.enrollUserInCourse(auth.getName(), courseId);
            }
            // Prioritize URL amount (may be discounted by voucher), fall back to course.price
            Double displayAmount = (amount != null && amount > 0)
                ? amount
                : (course.getPrice() != null ? course.getPrice() : 0.0);
            boolean freeDisplay = course.isIsFree() || displayAmount == 0;
            String paidAt = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
                    .format(new java.util.Date());
            model.addAttribute("course", course);
            model.addAttribute("lessonCount", lessonRepository.countByCourse_CourseId(courseId));
            model.addAttribute("txId", txId);
            model.addAttribute("priceFormatted", formatPrice(displayAmount, freeDisplay));
            model.addAttribute("paidAt", paidAt);
        } catch (Exception e) {
            return "redirect:/courses";
        }
        return "course/checkout-success";
    }

    /** Format price in Vietnamese style: 299.000đ or Miễn phí */
    private String formatPrice(Double price, boolean isFree) {
        if (isFree || price == null || price == 0) return "Miễn phí";
        long p = price.longValue();
        // Format with dot separator: 299.000đ
        String raw = String.format("%,d", p).replace(',', '.');
        return raw + "đ";
    }

    /**
     * MoMo Return URL handler — user is redirected here after paying on MoMo.
     * Reads resultCode + extraData, enrolls the user, then redirects to lessons.
     */
    @GetMapping("/payment/callback")
    @org.springframework.transaction.annotation.Transactional
    public String momoReturnCallback(
            @RequestParam Map<String, String> params,
            Model model) {
        model.addAttribute("pageTitle", "Kết quả thanh toán");

        try {
            if (params.containsKey("signature") && !momoService.verifyCallbackSignature(params)) {
                model.addAttribute("success", false);
                model.addAttribute("message", "Chữ ký callback không hợp lệ. Vui lòng liên hệ hỗ trợ.");
                model.addAttribute("resultCode", "INVALID_SIGNATURE");
                return "payment-sync/payment-callback";
            }

            String resultCode = params.getOrDefault("resultCode", "-1");
            String extraData = params.getOrDefault("extraData", "");
            Map<String, String> parsed = momoService.parseExtraData(extraData);

            String userId = parsed.get("userId");
            Long courseId = parsed.containsKey("courseId") ? Long.parseLong(parsed.get("courseId")) : null;

            String transactionId = params.get("orderId");
            if (transactionId == null || transactionId.isBlank()) {
                transactionId = params.get("transId");
            }

            if ("0".equals(resultCode)) {
                if (transactionId != null && !transactionId.isBlank()) {
                    Payment payment = paymentService.getPaymentByTransactionId(transactionId);
                    if (payment != null && payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
                        paymentService.updatePaymentStatus(payment.getPaymentId(), Payment.PaymentStatus.COMPLETED);
                    }
                } else if (courseId != null && userId != null) {
                    enrollmentService.enrollUserInCourse(userId, courseId);
                }

                model.addAttribute("success", true);
                model.addAttribute("message", "Thanh toán thành công. Bạn có thể bắt đầu học ngay.");
                model.addAttribute("transactionId", transactionId);
                model.addAttribute("courseId", courseId);
                model.addAttribute("redirectUrl", courseId != null ? "/lessons/course/" + courseId : "/dashboard");
                model.addAttribute("resultCode", resultCode);
                return "payment-sync/payment-callback";
            }

            if (transactionId != null && !transactionId.isBlank()) {
                Payment payment = paymentService.getPaymentByTransactionId(transactionId);
                if (payment != null && payment.getStatus() == Payment.PaymentStatus.PENDING) {
                    paymentService.updatePaymentStatus(payment.getPaymentId(), Payment.PaymentStatus.FAILED);
                }
            }

            model.addAttribute("success", false);
            model.addAttribute("message", "Thanh toán không thành công. Mã lỗi: " + resultCode);
            model.addAttribute("transactionId", transactionId);
            model.addAttribute("courseId", courseId);
            model.addAttribute("redirectUrl", courseId != null ? "/courses/" + courseId + "/checkout" : "/courses");
            model.addAttribute("resultCode", resultCode);
            return "payment-sync/payment-callback";
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Đã có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("resultCode", "SYSTEM_ERROR");
            return "payment-sync/payment-callback";
        }
    }

    @GetMapping("/lessons/{lessonId}")
    public String lessonDetail(@PathVariable Long lessonId, Model model) {
        try {
            Lesson lesson = lessonRepository.findByIdWithAll(lessonId)
                .orElseThrow(() -> new RuntimeException("Bài học không tồn tại"));
            
            // SECURITY CHECK: Must be enrolled in the course this lesson belongs to
            if (!isUserAuthorizedForCourse(lesson.getCourse().getCourseId())) {
                model.addAttribute("error", "Bạn chưa đăng ký khóa học này.");
                return "error/403_access_denied"; // Assuming this view exists or similar
            }

            model.addAttribute("lesson", lesson);
            model.addAttribute("pageTitle", lesson.getTitle() + " - Bài học");
        } catch (Exception e) {
            model.addAttribute("lesson", null);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Chi tiết bài học");
        }
        return "lesson/detail";
    }

    /**
     * Quiz page
     */
    @GetMapping("/quiz/{quizId}")
    public String quiz(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        model.addAttribute("pageTitle", "Quiz");
        return "lesson/quiz";
    }

    /**
     * About page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About - Japanese Learning Web");
        return "about";
    }

    /**
     * Vocabulary list page - display vocabularies filtered by course
     */
    @GetMapping("/vocabularies")
    public String vocabulariesList(@RequestParam(required = false) Long courseId, Model model) {
        try {
            // SECURITY: Only show courses user is authorized for
            List<Course> allCourses = courseRepository.findAll();
            List<Course> authorizedCourses = allCourses.stream()
                .filter(c -> isUserAuthorizedForCourse(c.getCourseId()))
                .collect(java.util.stream.Collectors.toList());
            
            model.addAttribute("courses", authorizedCourses);

            if (authorizedCourses.isEmpty()) {
                model.addAttribute("vocabularies", List.of());
                return "vocabularies/index";
            }

            // If courseId is provided, fetch vocabularies for that course
            if (courseId != null) {
                if (!isUserAuthorizedForCourse(courseId)) {
                    model.addAttribute("error", "Bạn chưa đăng ký khóa học này.");
                    return "error/403_access_denied";
                }
                Course course = courseService.getCourseEntityById(courseId);
                List<Vocabulary> vocabularies = vocabularyService.getVocabulariesByCourse(course);
                model.addAttribute("vocabularies", vocabularies);
                model.addAttribute("selectedCourseId", courseId);
            } else {
                // Show first authorized course by default if available
                Course firstCourse = authorizedCourses.get(0);
                List<Vocabulary> vocabularies = vocabularyService.getVocabulariesByCourse(firstCourse);
                model.addAttribute("vocabularies", vocabularies);
                model.addAttribute("selectedCourseId", firstCourse.getCourseId());
            }

            model.addAttribute("pageTitle", "Danh sách từ vựng - Japanese Learning Web");
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu từ vựng: " + e.getMessage());
            model.addAttribute("vocabularies", List.of());
        }
        return "vocabularies/index";
    }

    /**
     * Dedicated SRS review page for JL-40 UX.
     */
    @GetMapping("/vocabularies/srs-review")
    public String srsReviewPage(Model model) {
        model.addAttribute("pageTitle", "Ôn tập SRS - Japanese Learning");
        return "vocabularies/srs-review";
    }

    /**
     * Grammar list page
     */
    @GetMapping("/grammars")
    public String grammarsList(Model model) {
        try {
            // Use JOIN FETCH to eagerly load Lesson (avoids LazyInitializationException)
            List<Grammar> grammars = grammarRepository.findAllWithLesson();
            model.addAttribute("grammars", grammars);
        } catch (Exception e) {
            model.addAttribute("grammars", List.of());
        }
        model.addAttribute("pageTitle", "Ngữ pháp - Japanese Learning");
        return "grammars/index";
    }

    /**
     * Kanji practice page
     */
    @GetMapping("/kanjis")
    public String kanjisList(Model model) {
        try {
            List<Kanji> kanjis = kanjiRepository.findAll();
            model.addAttribute("kanjis", kanjis);
        } catch (Exception e) {
            model.addAttribute("kanjis", List.of());
        }
        model.addAttribute("pageTitle", "Kanji - Japanese Learning");
        return "kanji/list";
    }

    /**
     * Radicals (Bộ thủ) page
     */
    @GetMapping("/radicals")
    public String radicalsList(Model model) {
        try {
            // JOIN FETCH kanjis to avoid LazyInit
            List<KanjiRadical> radicals = kanjiRadicalRepository.findAllWithKanjis();
            model.addAttribute("radicals", radicals);
        } catch (Exception e) {
            model.addAttribute("radicals", List.of());
        }
        model.addAttribute("pageTitle", "Bộ thủ- Japanese Learning");
        return "radicals/index";
    }

    /**
     * Lessons portal - Show courses for study selection
     */
    @GetMapping("/lessons")
    public String lessonsPortal(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // SHOW ALL COURSES WITH LESSONS EAGERLY LOADED - DEDUPLICATED
            List<Course> courses = new java.util.ArrayList<>(new java.util.LinkedHashSet<>(courseRepository.findAllWithLessons()));
            
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
                
                java.util.Set<Long> enrolledCourseIds = new java.util.HashSet<>();
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    // Admins see everything as enrolled
                    for(Course c : courses) enrolledCourseIds.add(c.getCourseId());
                } else {
                    String userId = authentication.getName();
                    List<UserCourseEnrollment> enrollments = enrollmentService.getUserEnrollments(userId);
                    for(UserCourseEnrollment e : enrollments) enrolledCourseIds.add(e.getCourse().getCourseId());
                }
                model.addAttribute("enrolledCourseIds", enrolledCourseIds);
            }
            
            model.addAttribute("courses", courses);
        } catch (Exception e) {
            model.addAttribute("courses", List.of());
        }
        model.addAttribute("pageTitle", "Chọn khóa học - Japanese Learning");
        return "lesson/course-selection";
    }

    @GetMapping("/lessons/course/{courseId}")
    public String courseLessonsList(@PathVariable Long courseId, Model model) {
        try {
            // SECURITY CHECK: If NOT enrolled, send to checkout page instead of 403
            if (!isUserAuthorizedForCourse(courseId)) {
                return "redirect:/courses/" + courseId + "/checkout";
            }

            Course course = courseRepository.findById(courseId).orElse(null);
            // Updated to fetch collections for all lessons in this course to avoid LazyInit
            List<Lesson> lessons = lessonRepository.findByCourseIdWithCollections(courseId);
            model.addAttribute("lessons", lessons);
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", (course != null ? course.getTitle() : "Bài học") + " - Japanese Learning");
        } catch (Exception e) {
            model.addAttribute("lessons", List.of());
            model.addAttribute("pageTitle", "Bài học");
        }
        return "lesson/list";
    }

    /**
     * JL-35: Personalized course route page
     */
    @GetMapping("/courses/{courseId}/route")
    public String personalizedCourseRoute(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long currentLessonId,
            Model model) {
        try {
            if (!isUserAuthorizedForCourse(courseId)) {
                return "redirect:/courses/" + courseId + "/checkout";
            }

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Khóa học không tồn tại"));
            List<Lesson> lessons = lessonRepository.findByCourse_CourseIdOrderByOrderInCourseAsc(courseId);

            Lesson currentLesson = null;
            if (!lessons.isEmpty()) {
                if (currentLessonId != null) {
                    currentLesson = lessons.stream()
                            .filter(l -> l.getLessonId().equals(currentLessonId))
                            .findFirst()
                            .orElse(lessons.get(0));
                } else {
                    currentLesson = lessons.get(0);
                }
            }

            Lesson nextLesson = null;
            if (currentLesson != null) {
                for (int i = 0; i < lessons.size(); i++) {
                    if (lessons.get(i).getLessonId().equals(currentLesson.getLessonId()) && i + 1 < lessons.size()) {
                        nextLesson = lessons.get(i + 1);
                        break;
                    }
                }
            }

            int totalLessons = lessons.size();
            int currentOrder = currentLesson != null && currentLesson.getOrderInCourse() != null
                    ? currentLesson.getOrderInCourse()
                    : (currentLesson != null ? 1 : 0);
            int progressPercent = totalLessons > 0
                    ? Math.min((int) Math.round((currentOrder * 100.0) / totalLessons), 100)
                    : 0;

            model.addAttribute("course", course);
            model.addAttribute("lessons", lessons);
            model.addAttribute("currentLesson", currentLesson);
            model.addAttribute("nextLesson", nextLesson);
            model.addAttribute("progressPercent", progressPercent);
            model.addAttribute("totalLessons", totalLessons);
            model.addAttribute("currentOrder", currentOrder);
            model.addAttribute("pageTitle", "Lộ trình học - " + course.getTitle());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/lessons";
        }

        return "lesson/route";
    }

    /**
     * Dashboard page (user progress)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String userId = authentication.getName();
            // Get user's enrolled courses
            List<UserCourseEnrollment> enrolledCourses = enrollmentService.getUserEnrollments(userId);
            model.addAttribute("enrolledCourses", enrolledCourses);
        }
        
        // Get all available courses for suggestions
        var allCourses = courseService.getAllCourses();
        model.addAttribute("allCourses", allCourses);
        
        model.addAttribute("pageTitle", "Dashboard");
        return "dashboard";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "account/login";
    }

    /**
     * Register page
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "account/register";
    }

    /**
     * Forgot password page
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("pageTitle", "Forgot Password");
        return "account/forgot-password";
    }

    /**
     * User profile page
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("pageTitle", "Profile");
        return "account/profile";
    }

    /**
     * User enrolled courses page
     */
    @GetMapping("/my-courses")
    public String myCourses(Model model) {
        model.addAttribute("pageTitle", "My Courses");
        return "account/my-courses";
    }

    /**
     * Home login page (synced from legacy Views/Home/Login.cshtml)
     */
    @GetMapping("/home/login")
    public String homeLogin(Model model) {
        model.addAttribute("pageTitle", "Dang nhap");
        return "home/login";
    }

    /**
     * Home register page (synced from legacy Views/Home/Register.cshtml)
     */
    @GetMapping("/home/register")
    public String homeRegister(Model model) {
        model.addAttribute("pageTitle", "Dang ky");
        return "home/register";
    }

    /**
     * Home profile page (synced from legacy Views/Home/Profile.cshtml)
     */
    @GetMapping("/home/profile")
    public String homeProfile(Model model) {
        model.addAttribute("pageTitle", "Ho so ca nhan");
        return "home/profile";
    }

    /**
     * Home course lesson list page
     */
    @GetMapping("/home/course-lesson")
    public String homeCourseLesson(Model model) {
        model.addAttribute("pageTitle", "Danh sach khoa hoc");
        return "home/course-lesson";
    }

    /**
     * Home practice kanji page
     */
    @GetMapping("/home/practice-kanji")
    public String homePracticeKanji(Model model) {
        model.addAttribute("pageTitle", "Practice Kanji");
        return "home/practice-kanji";
    }

    /**
     * Error handling for 404
     */
    @GetMapping("/error/404")
    public String notFound(Model model) {
        model.addAttribute("pageTitle", "Page Not Found");
        return "error/404";
    }

    /**
     * Error handling for 500
     */
    @GetMapping("/error/500")
    public String internalError(Model model) {
        model.addAttribute("pageTitle", "Server Error");
        return "error/500";
    }

    /**
     * Helper to check if the current user is authorized to view content of a course.
     * Authorized if: Logged in AND (Enrolled OR is Admin)
     */
    private boolean isUserAuthorizedForCourse(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"))) {
            return false;
        }
        
        // Admins can see everything
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        String userId = authentication.getName();
        return enrollmentService.isUserEnrolledInCourse(userId, courseId);
    }
}
