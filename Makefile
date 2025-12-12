.PHONY: local prod rebuild clean

# Разработка с hot-reload
local:
	docker-compose -f compose.yml -f compose.local.yml up -d --build

# Продакшен сборка
prod:
	docker-compose up -d --build

# Пересборка при изменениях
rebuild:
	docker-compose build api
	docker-compose up -d

# Полная очистка
clean:
	docker-compose down -v --rmi all
	docker system prune -f
