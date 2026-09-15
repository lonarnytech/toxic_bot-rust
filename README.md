# The Decaying Code — Forge 1.20.1

Первый этап хоррор-мода: серверная система рассудка с сохранением, синхронизацией и пороговыми эффектами.

## Среда

- Minecraft `1.20.1`
- Forge `47.4.23`
- Java `17`
- Mojang official mappings
- Mod ID: `decaying_code`

## Структура первого этапа

```text
src/main/java/com/decayingcode/
├── TheDecayingCode.java                    # точка входа, регистрация сети и клиента
├── client/
│   ├── ClientModEvents.java                # RegisterGuiOverlaysEvent на MOD bus
│   ├── data/
│   │   └── ClientSanityData.java           # клиентское зеркало рассудка
│   └── gui/
│       └── EyeHudOverlay.java              # отрисовка глаза через GuiGraphics.blit
├── registry/
│   └── ModSounds.java                      # регистрация шепота и шагов
└── sanity/
    ├── capability/
    │   ├── PlayerSanity.java               # значение 0..100 и NBT
    │   ├── SanityCapability.java           # CapabilityToken
    │   ├── SanityProvider.java             # сериализация capability
    │   └── SanityCapabilityRegistration.java
    ├── event/
    │   ├── SanityCapabilityEvents.java     # attach/clone/login/respawn/dimension sync
    │   ├── SanityTickEvents.java           # свет, восстановление и эффекты
    │   └── ClientSanityEvents.java         # сброс кэша при выходе
    └── network/
        ├── SanityNetwork.java              # SimpleChannel SERVER -> CLIENT
        └── SanitySyncPacket.java

src/main/resources/
├── META-INF/mods.toml
└── assets/decaying_code/
    ├── sounds.json                         # пока использует ванильные sound events
    ├── lang/{ru_ru,en_us}.json
    └── textures/gui/eye.png                # физическая текстура HUD 16x16
```

## Правила рассудка

Проверка выполняется сервером раз в 100 тиков (5 секунд):

1. Днем под открытым небом или в радиусе 5 блоков по горизонтали и 3 по вертикали от факела/зажженного костра восстанавливается `2` единицы.
2. Иначе при комбинированном локальном освещении ниже `4` теряется `1` единица.
3. Между этими условиями значение не меняется.
4. Значение всегда ограничено диапазоном `0..100`, по умолчанию равно `100`.
5. При значении ниже `50` раз в пять секунд есть шанс 25% услышать шепот или шаги. Звук отправляется только затронутому игроку.
6. При значении ниже `20` накладывается слепота на 40 тиков (2 секунды).

Сервер — единственный источник истины. Клиентский HUD может читать значение через `ClientSanityData.getSanity()`.

## HUD глаза

Клиент регистрирует `EyeHudOverlay` через `RegisterGuiOverlaysEvent` непосредственно на MOD event bus. Иконка рисуется поверх ванильного HUD методом `GuiGraphics.blit()` в правом верхнем углу и скрывается вместе с интерфейсом по F1. Файл текстуры обязательно упаковывается как `assets/decaying_code/textures/gui/eye.png`.

## Запуск

```bash
./gradlew build
./gradlew runClient
```
