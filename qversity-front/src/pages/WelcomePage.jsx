import React from 'react';

// Helper component for Feature Cards
const FeatureCard = ({ icon, title, children }) => (
  <div className="feature-card bg-white p-6 rounded-lg shadow-md hover:shadow-xl transition-shadow">
    <div className="text-primary mb-4">{icon}</div>
    <h3 className="text-xl font-bold text-dark-color mb-2">{title}</h3>
    <p className="text-gray-600">{children}</p>
  </div>
);

// Main Welcome Page Component
export default function WelcomePage() {
  return (
    <div className="font-sans bg-light-color text-dark-color">
      {/* Header */}
      <header className="bg-white shadow-sm sticky top-0 z-50">
        <div className="container mx-auto px-6 py-3 flex justify-between items-center">
          <div className="text-2xl font-bold text-primary">Qversity</div>
          <nav className="hidden md:flex items-center space-x-6">
            <a href="#features" className="hover:text-primary">기능</a>
            <a href="#how-it-works" className="hover:text-primary">사용 방법</a>
            <a href="#" className="hover:text-primary">가격</a>
          </nav>
          <div className="hidden md:flex items-center space-x-2">
            <a href="/login" className="px-4 py-2 rounded-md hover:bg-gray-100">로그인</a>
            <a href="/login" className="bg-primary text-white px-4 py-2 rounded-md hover:bg-secondary">
              무료로 시작하기
            </a>
          </div>
          <div className="md:hidden">
            <button>Menu</button> {/* Placeholder for mobile menu */}
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="pt-24 pb-16">
        <div className="container mx-auto px-6 text-center">
          <h1 className="text-4xl md:text-6xl font-extrabold mb-4">
            더 효율적인 학습을 위한 <span className="text-primary">스마트 플래시카드</span>
          </h1>
          <p className="text-lg md:text-xl text-gray-600 max-w-3xl mx-auto mb-8">
            과학적으로 검증된 간격 반복 학습법으로 기억력을 향상시키고 학습 효율을 극대화합니다.
          </p>
          <button type="button" className="bg-primary text-white px-8 py-3 rounded-lg text-lg font-semibold hover:bg-secondary transition-colors">
            무료로 시작하기
          </button>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 bg-white">
        <div className="container mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold">학습 효율을 높이는 핵심 기능</h2>
            <p className="text-gray-600 mt-4 max-w-2xl mx-auto">
              단순한 암기 도구가 아닙니다. 과학적 학습 방법론으로 여러분의 학습 효율을 극대화합니다.
            </p>
          </div>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <FeatureCard title="간격 반복 학습 (SRS)" icon={<span>🧠</span>}>
              FSRS 알고리즘을 기반으로 최적의 복습 시점을 제안하여 장기 기억으로의 전환을 돕습니다.
            </FeatureCard>
            <FeatureCard title="맞춤형 학습 계획" icon={<span>📊</span>}>
              개인의 학습 패턴과 성취도에 맞춰 최적화된 학습 계획을 자동으로 생성하여 효율적인 학습을 지원합니다.
            </FeatureCard>
            <FeatureCard title="상세한 학습 분석" icon={<span>📈</span>}>
              학습 진행 상황과 성취도를 시각적으로 분석하여 제공함으로써 자신의 학습 패턴을 이해하고 개선할 수 있습니다.
            </FeatureCard>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section id="how-it-works" className="py-16">
        <div className="container mx-auto px-6">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold">간단한 4단계 사용법</h2>
          </div>
          <div className="flex flex-col md:flex-row justify-between items-center space-y-8 md:space-y-0 md:space-x-8">
            <div className="text-center">
              <div className="text-4xl font-bold text-primary bg-blue-100 w-20 h-20 mx-auto rounded-full flex items-center justify-center mb-4">1</div>
              <h3 className="text-xl font-bold mb-2">카드 만들기</h3>
              <p className="text-gray-600">직접 카드를 만들거나 공유된 카드를 활용하세요.</p>
            </div>
            <div className="text-center">
              <div className="text-4xl font-bold text-primary bg-blue-100 w-20 h-20 mx-auto rounded-full flex items-center justify-center mb-4">2</div>
              <h3 className="text-xl font-bold mb-2">학습하기</h3>
              <p className="text-gray-600">간격 반복 알고리즘으로 효율적으로 학습하세요.</p>
            </div>
            <div className="text-center">
              <div className="text-4xl font-bold text-primary bg-blue-100 w-20 h-20 mx-auto rounded-full flex items-center justify-center mb-4">3</div>
              <h3 className="text-xl font-bold mb-2">평가하기</h3>
              <p className="text-gray-600">Anki처럼 카드의 난이도를 직접 평가하세요.</p>
            </div>
            <div className="text-center">
              <div className="text-4xl font-bold text-primary bg-blue-100 w-20 h-20 mx-auto rounded-full flex items-center justify-center mb-4">4</div>
              <h3 className="text-xl font-bold mb-2">성장 확인</h3>
              <p className="text-gray-600">학습 분석을 통해 성장을 확인하세요.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-dark-color text-white">
        <div className="container mx-auto px-6 py-12">
          <div className="grid md:grid-cols-4 gap-8">
            <div>
              <h3 className="text-xl font-bold mb-4">Qversity</h3>
              <p className="text-gray-400">과학적 학습법으로 더 나은 학습 경험을 제공합니다.</p>
            </div>
            <div>
              <h3 className="font-bold mb-4">서비스</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white">기능 소개</a></li>
                <li><a href="#" className="hover:text-white">요금제</a></li>
              </ul>
            </div>
            <div>
              <h3 className="font-bold mb-4">회사</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white">소개</a></li>
                <li><a href="#" className="hover:text-white">연락처</a></li>
              </ul>
            </div>
            <div>
              <h3 className="font-bold mb-4">지원</h3>
              <ul className="space-y-2 text-gray-400">
                <li><a href="#" className="hover:text-white">고객센터</a></li>
                <li><a href="#" className="hover:text-white">이용약관</a></li>
              </ul>
            </div>
          </div>
          <div className="mt-12 border-t border-gray-700 pt-8 text-center text-gray-500">
            <p>&copy; {new Date().getFullYear()} Qversity. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}